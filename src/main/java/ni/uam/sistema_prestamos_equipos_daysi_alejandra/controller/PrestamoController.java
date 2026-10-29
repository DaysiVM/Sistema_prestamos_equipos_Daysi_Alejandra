package ni.uam.sistema_prestamos_equipos_daysi_alejandra.controller;

import jakarta.validation.Valid;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.PrestamoRequest;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.PrestamoResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoPrestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.service.PrestamoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
 * GET    -> USER y ADMIN
 * POST (préstamo) y PUT /devolver -> solo ADMIN
 */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // 200 OK. Filtro opcional: /api/prestamos?estado=ACTIVO
    @GetMapping
    public List<PrestamoResponse> listar(@RequestParam(required = false) EstadoPrestamo estado) {
        return prestamoService.listar(estado);
    }

    // 200 OK | 404 Not Found
    @GetMapping("/{id}")
    public PrestamoResponse obtener(@PathVariable Long id) {
        return prestamoService.obtener(id);
    }

    // 201 Created | 400 (datos/fechas inválidos) | 404 (equipo inexistente) | 409 (equipo ya PRESTADO)
    @PostMapping
    public ResponseEntity<PrestamoResponse> registrar(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse creado = prestamoService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    // 200 OK | 404 | 409 (ya devuelto)
    @PutMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }
}
