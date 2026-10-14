package cr.ac.una.reservas.excepcion;


public class ReglaDeNegocioException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaDeNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
