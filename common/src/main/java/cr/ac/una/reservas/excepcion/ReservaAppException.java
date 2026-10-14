package cr.ac.una.reservas.excepcion;


public class ReservaAppException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservaAppException(String mensaje) {
        super(mensaje);
    }

    public ReservaAppException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
