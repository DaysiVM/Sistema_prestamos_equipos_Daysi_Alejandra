package ni.uam.sistema_prestamos_equipos_daysi_alejandra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
 * 401 Unauthorized: el cliente NO está autenticado (no envió token, o el token es inválido/expirado).
 * "¿Quién eres?" -> no lo sabemos.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public RestAuthenticationEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object errorToken = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR_TOKEN);
        String mensaje = (errorToken != null)
                ? errorToken.toString()
                : "Se requiere autenticación: envíe un token JWT válido en la cabecera Authorization (Bearer)";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(
                ErrorResponse.of(401, "Unauthorized", mensaje, request.getRequestURI())));
    }
}
