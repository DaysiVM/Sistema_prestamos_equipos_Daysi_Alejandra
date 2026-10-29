package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/*
 * Formato único de error para toda la API (400, 401, 403, 404, 409, 500).
 * "detalles" solo aparece en errores de validación (campo -> mensaje).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> detalles
) {

    public static ErrorResponse of(int status, String error, String mensaje, String ruta) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje, ruta, null);
    }
}
