package ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository;

import jakarta.persistence.LockModeType;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    boolean existsByCodigoPatrimonialIgnoreCase(String codigoPatrimonial);

    boolean existsByCodigoPatrimonialIgnoreCaseAndIdNot(String codigoPatrimonial, Long id);

    /*
     * Bloquea la fila del equipo mientras se registra el préstamo (SELECT ... FOR UPDATE).
     * Así dos solicitudes simultáneas no pueden prestar el mismo equipo a la vez.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Equipo e where e.id = :id")
    Optional<Equipo> findByIdParaActualizar(@Param("id") Long id);
}
