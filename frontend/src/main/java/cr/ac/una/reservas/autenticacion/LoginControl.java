package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.principal.VentanaPrincipalControl;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.Frame;
import java.util.concurrent.ExecutionException;

public class LoginControl {

    private static final int TIEMPO_MAXIMO_MS = 10000;
    private static final String MENSAJE_TIEMPO_AGOTADO =
            "El servidor tardó demasiado en responder. Intente de nuevo en unos momentos.";

    private final LoginModel modelo;
    private final LoginPanel vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;
    private boolean ingresando;

    public LoginControl(LoginModel modelo, LoginPanel vista, Frame ventanaPropietaria) {
        this(modelo, vista, ventanaPropietaria, ServiceFactory.obtenerAutenticacionService());
    }

    public LoginControl(
            LoginModel modelo, LoginPanel vista, Frame ventanaPropietaria, AutenticacionService autenticacionService
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.autenticacionService = autenticacionService;
        this.vista.alIngresar(this::intentarIngresar);
        this.vista.alCambiarClave(this::abrirCambioClave);
        this.vista.alCerrar(this::confirmarSalidaDeAplicacion);
    }

    private void confirmarSalidaDeAplicacion() {
        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Salir del sistema");
        popup.setMensaje("¿Seguro desea salir del Sistema de Reserva de Recursos?");
        java.util.List<Popup.AccionPopup> acciones = new java.util.ArrayList<>();
        acciones.add(new Popup.AccionPopup("Cancelar", false, null));
        acciones.add(new Popup.AccionPopup("Salir", true, () -> System.exit(0)));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void intentarIngresar() {
        if (ingresando) {
            return;
        }
        String id = vista.obtenerId();
        String clave = vista.obtenerContrasena();

        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            modelo.setMensajeError("Debe ingresar el ID y la contraseña.");
            return;
        }

        ingresando = true;
        vista.setCargando(true);
        String idLimpio = id.trim();
        boolean[] vencido = {false};
        Timer limite = new Timer(TIEMPO_MAXIMO_MS, evento -> {
            vencido[0] = true;
            terminarCarga();
            modelo.setMensajeError(MENSAJE_TIEMPO_AGOTADO);
        });
        limite.setRepeats(false);
        limite.start();
        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() {
                return autenticacionService.autenticar(idLimpio, clave);
            }

            @Override
            protected void done() {
                limite.stop();
                if (vencido[0]) {
                    return;
                }
                terminarCarga();
                try {
                    Usuario usuario = get();
                    SesionControl.obtenerInstancia().iniciarSesion(usuario);
                    navegarAVentanaPrincipal();
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException excepcion) {
                    if (excepcion.getCause() instanceof ReservaAppException causa) {
                        mostrarFalla(causa);
                    } else {
                        throw new IllegalStateException(excepcion.getCause());
                    }
                }
            }
        }.execute();
    }

    private void terminarCarga() {
        ingresando = false;
        vista.setCargando(false);
    }

    private void mostrarFalla(ReservaAppException excepcion) {
        if (excepcion instanceof ConexionException || excepcion instanceof SesionNoIniciadaException) {
            ReaccionExcepcion.mostrar(ventanaPropietaria, "No se pudo completar la acción", excepcion);
            return;
        }
        modelo.setMensajeError(excepcion.getMessage());
    }

    private void navegarAVentanaPrincipal() {
        if (ventanaPropietaria instanceof JFrame) {
            VentanaPrincipalControl.mostrarEn((JFrame) ventanaPropietaria);
        }
    }

    private void abrirCambioClave() {
        DialogoCambioClave dialogo = new DialogoCambioClave(ventanaPropietaria);
        dialogo.alConfirmar(() -> confirmarCambioClave(dialogo));
        dialogo.mostrar();
    }

    private void confirmarCambioClave(DialogoCambioClave dialogo) {
        String id = dialogo.obtenerId();
        if (id == null || id.isBlank()) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña",
                    "Debe ingresar el ID del usuario."
            );
            return;
        }
        try {
            autenticacionService.cambiarClave(id.trim(), dialogo.obtenerClaveActual(), dialogo.obtenerClaveNueva());
            dialogo.cerrar();
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.CONFIRMACION, "Cambiar contraseña",
                    "La contraseña se actualizó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            ReaccionExcepcion.mostrar(
                    ventanaPropietaria, "No se pudo cambiar la contraseña", excepcion
            );
        }
    }
}
