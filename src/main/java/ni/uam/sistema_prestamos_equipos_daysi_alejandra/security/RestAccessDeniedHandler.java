package ni.uam.sistema_prestamos_equipos_daysi_alejandra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
 * 403 Forbidden: el cliente SÍ está autenticado (token válido), pero su rol no tiene permiso.
 * Ejemplo: un USER intenta crear (POST) un equipo.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public RestAccessDeniedHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(
                ErrorResponse.of(403, "Forbidden",
                        "Acceso denegado: su rol no tiene permiso para realizar esta operación",
                        request.getRequestURI())));
    }
}
