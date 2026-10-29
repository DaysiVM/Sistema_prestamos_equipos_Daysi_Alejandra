package ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dueño de la relación N:1: muchos préstamos pueden pertenecer al mismo equipo a lo largo del tiempo.
    @NotNull(message = "El equipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @NotBlank(message = "El nombre del solicitante es obligatorio")
    @Size(max = 120)
    @Column(name = "nombre_solicitante", nullable = false, length = 120)
    private String nombreSolicitante;

    @NotNull(message = "La fecha de préstamo es obligatoria")
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    // La regla "no puede ser anterior a la fecha del préstamo" se valida en PrestamoRequest y PrestamoService.
    @NotNull(message = "La fecha prevista de devolución es obligatoria")
    @Column(name = "fecha_prevista_devolucion", nullable = false)
    private LocalDate fechaPrevistaDevolucion;

    // Fecha real en que se registró la devolución (null mientras el préstamo está ACTIVO).
    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @NotNull(message = "El estado del préstamo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    public Prestamo() {
    }

    public Long getId() {
        return id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaPrevistaDevolucion() {
        return fechaPrevistaDevolucion;
    }

    public void setFechaPrevistaDevolucion(LocalDate fechaPrevistaDevolucion) {
        this.fechaPrevistaDevolucion = fechaPrevistaDevolucion;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }
}
