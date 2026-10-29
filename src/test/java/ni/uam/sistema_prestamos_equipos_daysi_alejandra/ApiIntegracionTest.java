package ni.uam.sistema_prestamos_equipos_daysi_alejandra;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Pruebas de integración de punta a punta (seguridad + controladores + servicios + JPA sobre H2).
 * Cubren los casos que pide el examen: códigos HTTP, roles y reglas de negocio.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegracionTest {

    private static final AtomicInteger SECUENCIA = new AtomicInteger();

    @Autowired
    private MockMvc mvc;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void obtenerTokens() throws Exception {
        tokenAdmin = login("admin", "Admin123*");
        tokenUser = login("usuario", "Usuario123*");
    }

    // ---------- Reto 2: seguridad ----------

    @Test
    void getEquiposSinToken_devuelve401() throws Exception {
        mvc.perform(get("/api/equipos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void getEquiposConTokenInvalido_devuelve401() throws Exception {
        mvc.perform(get("/api/equipos").header("Authorization", "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEquiposConTokenUser_devuelve200() throws Exception {
        mvc.perform(get("/api/equipos").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isOk());
    }

    @Test
    void postEquipoConTokenUser_devuelve403() throws Exception {
        mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigoNuevo(), "Laptop de prueba", "PORTATIL")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void postEquipoConTokenAdmin_devuelve201() throws Exception {
        mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigoNuevo(), "Laptop de prueba", "PORTATIL")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void loginConCredencialesIncorrectas_devuelve401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Reto 1: CRUD, validaciones y reglas de negocio ----------

    @Test
    void crudCompletoDeEquipo() throws Exception {
        long id = crearEquipo(codigoNuevo());

        mvc.perform(get("/api/equipos/" + id).header("Authorization", bearer(tokenUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mvc.perform(put("/api/equipos/" + id).header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigoNuevo(), "Nombre actualizado", "TABLETA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre actualizado"))
                .andExpect(jsonPath("$.tipo").value("TABLETA"));

        mvc.perform(delete("/api/equipos/" + id).header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/equipos/" + id).header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void datosInvalidos_devuelve400() throws Exception {
        mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPatrimonial\":\"\",\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.codigoPatrimonial").exists())
                .andExpect(jsonPath("$.detalles.tipo").exists());

        mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigoNuevo(), "Equipo", "IMPRESORA")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void codigoPatrimonialDuplicado_devuelve409() throws Exception {
        String codigo = codigoNuevo();
        crearEquipo(codigo);
        mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigo, "Otro", "PROYECTOR")))
                .andExpect(status().isConflict());
    }

    @Test
    void equipoInexistente_devuelve404() throws Exception {
        mvc.perform(get("/api/equipos/999999").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void flujoCompletoDePrestamoYDevolucion() throws Exception {
        long equipoId = crearEquipo(codigoNuevo());
        LocalDate hoy = LocalDate.now();

        // Préstamo: 201 y el equipo pasa a PRESTADO
        MvcResult resultado = mvc.perform(post("/api/prestamos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(equipoId, hoy, hoy.plusDays(7))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.equipo.estado").value("PRESTADO"))
                .andReturn();
        long prestamoId = ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), "$.id")).longValue();

        // Prestar nuevamente el mismo equipo: 409
        mvc.perform(post("/api/prestamos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(equipoId, hoy, hoy.plusDays(3))))
                .andExpect(status().isConflict());

        // Eliminar equipo con préstamo activo: 409
        mvc.perform(delete("/api/equipos/" + equipoId).header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isConflict());

        // USER no puede registrar devoluciones: 403
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());

        // Devolución: 200 y el equipo vuelve a DISPONIBLE
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"))
                .andExpect(jsonPath("$.equipo.estado").value("DISPONIBLE"));

        // Devolver dos veces: 409
        mvc.perform(put("/api/prestamos/" + prestamoId + "/devolver").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isConflict());

        // Sin préstamos activos, la eliminación sí se permite: 204
        mvc.perform(delete("/api/equipos/" + equipoId).header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void fechaPrevistaAnteriorAlPrestamo_devuelve400() throws Exception {
        long equipoId = crearEquipo(codigoNuevo());
        LocalDate hoy = LocalDate.now();
        mvc.perform(post("/api/prestamos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(equipoId, hoy, hoy.minusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void prestamoDeEquipoInexistente_devuelve404() throws Exception {
        LocalDate hoy = LocalDate.now();
        mvc.perform(post("/api/prestamos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prestamoJson(999999L, hoy, hoy.plusDays(1))))
                .andExpect(status().isNotFound());
    }

    // ---------- utilidades ----------

    private String login(String username, String password) throws Exception {
        MvcResult resultado = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    private long crearEquipo(String codigo) throws Exception {
        MvcResult resultado = mvc.perform(post("/api/equipos").header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipoJson(codigo, "Equipo " + codigo, "PORTATIL")))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private static String codigoNuevo() {
        return "TEST-" + SECUENCIA.incrementAndGet();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String equipoJson(String codigo, String nombre, String tipo) {
        return "{\"codigoPatrimonial\":\"" + codigo + "\",\"nombre\":\"" + nombre + "\",\"tipo\":\"" + tipo + "\"}";
    }

    private static String prestamoJson(long equipoId, LocalDate inicio, LocalDate fin) {
        return "{\"equipoId\":" + equipoId + ",\"nombreSolicitante\":\"Instructor de prueba\","
                + "\"fechaPrestamo\":\"" + inicio + "\",\"fechaPrevistaDevolucion\":\"" + fin + "\"}";
    }
}
