package ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto;

public record LoginResponse(
        String token,
        String tipo,
        String username,
        String rol,
        long expiraEnSegundos
) {
}
