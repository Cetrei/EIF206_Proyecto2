package cr.ac.una.reservas.excepcion;

public class SesionNoIniciadaException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public SesionNoIniciadaException(String mensaje) {
        super(mensaje);
    }
}
