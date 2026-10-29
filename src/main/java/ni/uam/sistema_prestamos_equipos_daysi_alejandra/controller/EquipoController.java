package ni.uam.sistema_prestamos_equipos_daysi_alejandra.controller;

import jakarta.validation.Valid;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.EquipoRequest;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.EquipoResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.service.EquipoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
 * GET    -> USER y ADMIN
 * POST, PUT, DELETE -> solo ADMIN (las reglas están en SecurityConfig)
 */
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    // 200 OK
    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    // 200 OK | 404 Not Found
    @GetMapping("/{id}")
    public EquipoResponse obtener(@PathVariable Long id) {
        return equipoService.obtener(id);
    }

    // 201 Created (con cabecera Location) | 400 Bad Request | 409 Conflict (código duplicado)
    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody EquipoRequest request) {
        EquipoResponse creado = equipoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    // 200 OK | 400 | 404 | 409
    @PutMapping("/{id}")
    public EquipoResponse actualizar(@PathVariable Long id, @Valid @RequestBody EquipoRequest request) {
        return equipoService.actualizar(id, request);
    }

    // 204 No Content | 404 | 409 (tiene préstamo activo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
