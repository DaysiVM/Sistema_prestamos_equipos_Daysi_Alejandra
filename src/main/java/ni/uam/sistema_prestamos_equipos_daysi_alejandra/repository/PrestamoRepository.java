package ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoPrestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Prestamo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // @EntityGraph carga el equipo en la misma consulta (evita el problema N+1).
    @Override
    @EntityGraph(attributePaths = "equipo")
    List<Prestamo> findAll(Sort sort);

    @EntityGraph(attributePaths = "equipo")
    List<Prestamo> findByEstado(EstadoPrestamo estado, Sort sort);

    @Override
    @EntityGraph(attributePaths = "equipo")
    Optional<Prestamo> findById(Long id);

    boolean existsByEquipoIdAndEstado(Long equipoId, EstadoPrestamo estado);
}
