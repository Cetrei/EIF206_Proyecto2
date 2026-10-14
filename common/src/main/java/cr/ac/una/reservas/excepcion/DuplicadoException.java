package cr.ac.una.reservas.excepcion;

public class DuplicadoException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
