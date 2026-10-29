package ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
