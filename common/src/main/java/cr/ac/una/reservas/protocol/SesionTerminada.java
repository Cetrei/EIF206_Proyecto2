package cr.ac.una.reservas.protocol;

public final class SesionTerminada implements Notificacion {
    private static final long serialVersionUID = 1L;

    private final String mensaje;

    public SesionTerminada(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
