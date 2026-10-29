package ni.uam.sistema_prestamos_equipos_daysi_alejandra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * Se ejecuta una vez por solicitud, antes de las reglas de autorización:
 * 1. Lee la cabecera "Authorization: Bearer <token>".
 * 2. Valida la firma y la expiración del JWT.
 * 3. Si es válido, registra al usuario y su rol (ROLE_USER / ROLE_ADMIN) en el SecurityContext.
 * Si falta o es inválido, no autentica: la regla de acceso responderá 401.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR_TOKEN = "jwt.error";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith(PREFIJO)) {
            String token = cabecera.substring(PREFIJO.length()).trim();
            try {
                Claims claims = jwtService.validarToken(token);
                String rol = claims.get(JwtService.CLAIM_ROL, String.class);
                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                var autenticacion = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
                request.setAttribute(ATRIBUTO_ERROR_TOKEN, "El token es inválido o ha expirado");
            }
        }
        chain.doFilter(request, response);
    }
}
