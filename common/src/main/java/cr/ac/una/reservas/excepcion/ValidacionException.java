package cr.ac.una.reservas.excepcion;

public class ValidacionException extends ReservaAppException {
    private static final long serialVersionUID = 1L;

    private final String campo;

    public ValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
