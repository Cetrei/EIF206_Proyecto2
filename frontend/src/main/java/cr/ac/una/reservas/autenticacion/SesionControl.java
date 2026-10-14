package cr.ac.una.reservas.autenticacion;


public final class SesionControl {

    private static final SesionControl INSTANCIA = new SesionControl();

    private Usuario usuarioActual;
    private Runnable alCerrarSesion = () -> { };

    private SesionControl() {
    }

    public static SesionControl obtenerInstancia() {
        return INSTANCIA;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void alCerrarSesion(Runnable accion) {
        this.alCerrarSesion = accion;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
        alCerrarSesion.run();
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public Usuario usuarioActual() {
        return usuarioActual;
    }

    public RolUsuario rolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }

    public boolean esAdministrador() {
        return rolActual() == RolUsuario.ADMINISTRADOR;
    }
}
