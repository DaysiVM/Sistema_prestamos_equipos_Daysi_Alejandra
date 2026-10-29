package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.TipoEquipo;

/*
 * Datos que el cliente envía para crear o modificar un equipo.
 * El estado no se recibe: lo controla el sistema (DISPONIBLE al crear, PRESTADO/DISPONIBLE con los préstamos).
 */
public record EquipoRequest(

        @NotBlank(message = "El código patrimonial es obligatorio")
        @Size(max = 50, message = "El código patrimonial admite máximo 50 caracteres")
        String codigoPatrimonial,

        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
        String nombre,

        @NotNull(message = "El tipo de equipo es obligatorio (PORTATIL, PROYECTOR o TABLETA)")
        TipoEquipo tipo
) {
}
