package ni.uam.sistema_prestamos_equipos_daysi_alejandra.exception;

// Se traduce a 404 Not Found.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
