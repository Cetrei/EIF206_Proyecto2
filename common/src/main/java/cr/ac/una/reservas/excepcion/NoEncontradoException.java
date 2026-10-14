package cr.ac.una.reservas.excepcion;

public class NoEncontradoException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
