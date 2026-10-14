package cr.ac.una.reservas.excepcion;

public class AutorizacionException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public AutorizacionException(String mensaje) {
        super(mensaje);
    }
}
