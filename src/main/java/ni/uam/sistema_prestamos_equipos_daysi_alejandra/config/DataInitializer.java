package ni.uam.sistema_prestamos_equipos_daysi_alejandra.config;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Equipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Rol;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.TipoEquipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Usuario;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.EquipoRepository;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/*
 * Datos mínimos para la demostración. Se ejecuta al iniciar y solo inserta lo que falta:
 * - Cuentas de prueba ADMIN y USER (contraseñas configurables por variables de entorno, guardadas con BCrypt).
 * - Equipos de ejemplo, únicamente si la tabla de equipos está vacía.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String userPassword;
    private final boolean equiposDemo;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           EquipoRepository equipoRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed.admin-password}") String adminPassword,
                           @Value("${app.seed.user-password}") String userPassword,
                           @Value("${app.seed.equipos-demo:true}") boolean equiposDemo) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
        this.equiposDemo = equiposDemo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearUsuarioSiNoExiste("admin", adminPassword, Rol.ADMIN);
        crearUsuarioSiNoExiste("usuario", userPassword, Rol.USER);

        if (equiposDemo && equipoRepository.count() == 0) {
            crearEquipo("UAM-LAP-001", "Laptop Dell Latitude 5440", TipoEquipo.PORTATIL);
            crearEquipo("UAM-LAP-002", "Laptop HP ProBook 450 G10", TipoEquipo.PORTATIL);
            crearEquipo("UAM-PRO-001", "Proyector Epson PowerLite X49", TipoEquipo.PROYECTOR);
            crearEquipo("UAM-PRO-002", "Proyector BenQ MW560", TipoEquipo.PROYECTOR);
            crearEquipo("UAM-TAB-001", "Tableta Samsung Galaxy Tab S9", TipoEquipo.TABLETA);
            log.info("Se registraron 5 equipos de demostración");
        }
    }

    private void crearUsuarioSiNoExiste(String username, String password, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            usuarioRepository.save(new Usuario(username, passwordEncoder.encode(password), rol));
            log.info("Usuario de prueba '{}' creado con rol {}", username, rol);
        }
    }

    private void crearEquipo(String codigo, String nombre, TipoEquipo tipo) {
        Equipo equipo = new Equipo();
        equipo.setCodigoPatrimonial(codigo);
        equipo.setNombre(nombre);
        equipo.setTipo(tipo);
        equipoRepository.save(equipo);
    }
}
