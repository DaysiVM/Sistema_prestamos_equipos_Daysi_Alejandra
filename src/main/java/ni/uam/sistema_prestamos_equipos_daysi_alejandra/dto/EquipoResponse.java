package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Equipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoEquipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.TipoEquipo;

public record EquipoResponse(
        Long id,
        String codigoPatrimonial,
        String nombre,
        TipoEquipo tipo,
        EstadoEquipo estado
) {

    public static EquipoResponse desde(Equipo equipo) {
        return new EquipoResponse(
                equipo.getId(),
                equipo.getCodigoPatrimonial(),
                equipo.getNombre(),
                equipo.getTipo(),
                equipo.getEstado()
        );
    }
}
