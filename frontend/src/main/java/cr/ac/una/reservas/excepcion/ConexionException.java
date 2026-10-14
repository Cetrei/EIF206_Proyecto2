package cr.ac.una.reservas.excepcion;

public class ConexionException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public ConexionException(String mensaje) {
        super(mensaje);
    }

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
