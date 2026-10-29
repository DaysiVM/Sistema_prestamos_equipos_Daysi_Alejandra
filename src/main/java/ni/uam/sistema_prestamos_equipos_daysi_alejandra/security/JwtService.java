package ni.uam.sistema_prestamos_equipos_daysi_alejandra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
 * Emite y valida los JWT firmados con HMAC-SHA256.
 * El secreto llega desde la variable de entorno JWT_SECRET (nunca está escrito en el repositorio).
 */
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiration-ms}") long expiracionMs) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres (256 bits) para HS256");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROL, rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /**
     * Verifica la firma y la expiración. Lanza JwtException si el token es inválido o expiró.
     */
    public Claims validarToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMs / 1000;
    }
}
