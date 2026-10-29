package ni.uam.sistema_prestamos_equipos_daysi_alejandra.controller;

import jakarta.validation.Valid;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.LoginRequest;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.LoginResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // 200 OK con el token | 400 si faltan campos | 401 si las credenciales son incorrectas
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // Si usuario/contraseña no coinciden, lanza BadCredentialsException -> 401 (GlobalExceptionHandler).
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String rol = autenticacion.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        String token = jwtService.generarToken(autenticacion.getName(), rol);

        return new LoginResponse(token, "Bearer", autenticacion.getName(), rol, jwtService.getExpiracionSegundos());
    }
}
