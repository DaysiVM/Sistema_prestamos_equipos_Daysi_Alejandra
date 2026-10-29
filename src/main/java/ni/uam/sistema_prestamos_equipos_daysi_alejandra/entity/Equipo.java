package ni.uam.sistema_prestamos_equipos_daysi_alejandra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código patrimonial es obligatorio")
    @Size(max = 50)
    @Column(name = "codigo_patrimonial", nullable = false, unique = true, length = 50)
    private String codigoPatrimonial;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El tipo de equipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEquipo tipo;

    @NotNull(message = "El estado del equipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEquipo estado = EstadoEquipo.DISPONIBLE;

    /*
     * Lado inverso de la relación 1:N (un equipo tiene muchos préstamos en su historial).
     * El dueño de la relación es Prestamo.equipo (tiene la clave foránea equipo_id).
     * Si se elimina un equipo sin préstamos activos, se elimina también su historial devuelto.
     */
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Prestamo> prestamos = new ArrayList<>();

    public Equipo() {
    }

    public Long getId() {
        return id;
    }

    public String getCodigoPatrimonial() {
        return codigoPatrimonial;
    }

    public void setCodigoPatrimonial(String codigoPatrimonial) {
        this.codigoPatrimonial = codigoPatrimonial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }
}
