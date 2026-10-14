package cr.ac.una.reservas.excepcion;

public class CredencialesInvalidasException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
