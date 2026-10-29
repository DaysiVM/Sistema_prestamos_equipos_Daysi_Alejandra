package ni.uam.sistema_prestamos_equipos_daysi_alejandra.service;

import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.EquipoRequest;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.dto.EquipoResponse;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.Equipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoEquipo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity.EstadoPrestamo;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception.ConflictoNegocioException;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception.RecursoNoEncontradoException;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.EquipoRepository;
import ni.uam.sistema_prestamos_equipos_daysi_alejandra.repository.PrestamoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;

    public EquipoService(EquipoRepository equipoRepository, PrestamoRepository prestamoRepository) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAll(Sort.by("id")).stream()
                .map(EquipoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipoResponse obtener(Long id) {
        return EquipoResponse.desde(buscar(id));
    }

    @Transactional
    public EquipoResponse crear(EquipoRequest request) {
        String codigo = request.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonialIgnoreCase(codigo)) {
            throw new ConflictoNegocioException("Ya existe un equipo con el código patrimonial " + codigo);
        }

        Equipo equipo = new Equipo();
        equipo.setCodigoPatrimonial(codigo);
        equipo.setNombre(request.nombre().trim());
        equipo.setTipo(request.tipo());
        equipo.setEstado(EstadoEquipo.DISPONIBLE); // todo equipo nuevo inicia disponible

        return EquipoResponse.desde(equipoRepository.save(equipo));
    }

    @Transactional
    public EquipoResponse actualizar(Long id, EquipoRequest request) {
        Equipo equipo = buscar(id);
        String codigo = request.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonialIgnoreCaseAndIdNot(codigo, id)) {
            throw new ConflictoNegocioException("Ya existe otro equipo con el código patrimonial " + codigo);
        }

        // El estado no se modifica aquí: solo cambia al prestar o devolver.
        equipo.setCodigoPatrimonial(codigo);
        equipo.setNombre(request.nombre().trim());
        equipo.setTipo(request.tipo());

        return EquipoResponse.desde(equipo);
    }

    @Transactional
    public void eliminar(Long id) {
        Equipo equipo = buscar(id);
        // Regla de negocio: no se elimina un equipo con préstamos activos.
        if (prestamoRepository.existsByEquipoIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new ConflictoNegocioException(
                    "No se puede eliminar el equipo " + equipo.getCodigoPatrimonial() + " porque tiene un préstamo activo");
        }
        equipoRepository.delete(equipo);
    }

    private Equipo buscar(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un equipo con id " + id));
    }
}
