package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoPrestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Prestamo;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        EquipoResponse equipo,
        String nombreSolicitante,
        LocalDate fechaPrestamo,
        LocalDate fechaPrevistaDevolucion,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado
) {

    public static PrestamoResponse desde(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                EquipoResponse.desde(prestamo.getEquipo()),
                prestamo.getNombreSolicitante(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaPrevistaDevolucion(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado()
        );
    }
}
