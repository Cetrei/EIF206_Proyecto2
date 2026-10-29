package cr.ac.una.reservas.excepcion;

public class ErrorInternoException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    public static final String MENSAJE_GENERICO = "Ocurrió un error interno, intente de nuevo.";

    public ErrorInternoException() {
        super(MENSAJE_GENERICO);
    }

    public ErrorInternoException(String mensaje) {
        super(mensaje);
    }
}
