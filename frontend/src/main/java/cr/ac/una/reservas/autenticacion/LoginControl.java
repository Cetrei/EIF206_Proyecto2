package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.principal.VentanaPrincipalControl;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.JFrame;
import java.awt.Frame;

public class LoginControl {

    private final LoginModel modelo;
    private final LoginPanel vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;

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
        String id = vista.obtenerId();
        String clave = vista.obtenerContrasena();

        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            modelo.setMensajeError("Debe ingresar el ID y la contraseña.");
            return;
        }

        try {
            Usuario usuario = autenticacionService.autenticar(id.trim(), clave);
            SesionControl.obtenerInstancia().iniciarSesion(usuario);
            navegarAVentanaPrincipal();
        } catch (ReservaAppException excepcion) {
            if (excepcion instanceof ConexionException || excepcion instanceof SesionNoIniciadaException) {
                ReaccionExcepcion.mostrar(ventanaPropietaria, "No se pudo completar la acción", excepcion);
                return;
            }
            modelo.setMensajeError(excepcion.getMessage());
        }
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
