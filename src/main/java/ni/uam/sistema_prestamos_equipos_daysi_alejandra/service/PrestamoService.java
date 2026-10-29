package ni.uam.sistema_prestamos_equipos_daysi_alejandra.service;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.PrestamoRequest;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.PrestamoResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Equipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoEquipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoPrestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Prestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception.ConflictoNegocioException;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception.RecursoNoEncontradoException;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.EquipoRepository;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.PrestamoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private static final Sort ORDEN = Sort.by(Sort.Direction.DESC, "id");

    private final PrestamoRepository prestamoRepository;
    private final EquipoRepository equipoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, EquipoRepository equipoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.equipoRepository = equipoRepository;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar(EstadoPrestamo estado) {
        List<Prestamo> prestamos = (estado == null)
                ? prestamoRepository.findAll(ORDEN)
                : prestamoRepository.findByEstado(estado, ORDEN);
        return prestamos.stream().map(PrestamoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtener(Long id) {
        return PrestamoResponse.desde(buscar(id));
    }

    @Transactional
    public PrestamoResponse registrar(PrestamoRequest request) {
        // Segunda línea de defensa de la regla de fechas (la primera es @AssertTrue en el DTO).
        if (request.fechaPrevistaDevolucion().isBefore(request.fechaPrestamo())) {
            throw new IllegalArgumentException("La fecha prevista de devolución no puede ser anterior a la fecha del préstamo");
        }

        Equipo equipo = equipoRepository.findByIdParaActualizar(request.equipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un equipo con id " + request.equipoId()));

        // Regla de negocio: no se presta un equipo que ya está PRESTADO (o que tiene un préstamo activo).
        if (equipo.getEstado() == EstadoEquipo.PRESTADO
                || prestamoRepository.existsByEquipoIdAndEstado(equipo.getId(), EstadoPrestamo.ACTIVO)) {
            throw new ConflictoNegocioException(
                    "El equipo " + equipo.getCodigoPatrimonial() + " ya está PRESTADO y no puede prestarse nuevamente");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setEquipo(equipo);
        prestamo.setNombreSolicitante(request.nombreSolicitante().trim());
        prestamo.setFechaPrestamo(request.fechaPrestamo());
        prestamo.setFechaPrevistaDevolucion(request.fechaPrevistaDevolucion());
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        // Al registrar el préstamo, el equipo pasa a PRESTADO (misma transacción).
        equipo.setEstado(EstadoEquipo.PRESTADO);

        return PrestamoResponse.desde(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = buscar(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ConflictoNegocioException("El préstamo " + id + " ya fue devuelto");
        }

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());
        // Al registrar la devolución, el equipo vuelve a DISPONIBLE.
        prestamo.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);

        return PrestamoResponse.desde(prestamo);
    }

    private Prestamo buscar(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un préstamo con id " + id));
    }
}
