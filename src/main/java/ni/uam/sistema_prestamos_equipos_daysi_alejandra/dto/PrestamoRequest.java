package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PrestamoRequest(

        @NotNull(message = "El id del equipo es obligatorio")
        @Positive(message = "El id del equipo debe ser un número positivo")
        Long equipoId,

        @NotBlank(message = "El nombre del solicitante es obligatorio")
        @Size(max = 120, message = "El nombre del solicitante admite máximo 120 caracteres")
        String nombreSolicitante,

        @NotNull(message = "La fecha de préstamo es obligatoria (formato yyyy-MM-dd)")
        LocalDate fechaPrestamo,

        @NotNull(message = "La fecha prevista de devolución es obligatoria (formato yyyy-MM-dd)")
        LocalDate fechaPrevistaDevolucion
) {

    // Regla de negocio: la fecha prevista de devolución no puede ser anterior a la fecha del préstamo.
    @AssertTrue(message = "La fecha prevista de devolución no puede ser anterior a la fecha del préstamo")
    public boolean isFechaPrevistaDevolucionValida() {
        if (fechaPrestamo == null || fechaPrevistaDevolucion == null) {
            return true; // de los nulos se encargan las validaciones @NotNull
        }
        return !fechaPrevistaDevolucion.isBefore(fechaPrestamo);
    }
}
