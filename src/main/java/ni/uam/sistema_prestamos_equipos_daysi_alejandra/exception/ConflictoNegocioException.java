package ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception;

// Se traduce a 409 Conflict: la solicitud es válida, pero choca con una regla de negocio.
public class ConflictoNegocioException extends RuntimeException {

    public ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}
