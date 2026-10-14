package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.Main;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.ui.componentes.PanelCuenta;
import cr.ac.una.reservas.ui.componentes.Popup;
import cr.ac.una.reservas.ui.tema.GestorTema;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Frame;

final class CuentaControl {
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;
    private final FuncionarioService funcionarioService;

    CuentaControl(
            Frame ventanaPropietaria,
            AutenticacionService autenticacionService,
            FuncionarioService funcionarioService
    ) {
        this.ventanaPropietaria = ventanaPropietaria;
        this.autenticacionService = autenticacionService;
        this.funcionarioService = funcionarioService;
    }

    void abrirMiCuenta() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) return;

        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.setModoAdmin(false);

        String nombre = usuario instanceof Funcionario ? ((Funcionario) usuario).getNombre() : usuario.getId();
        String telefono = usuario instanceof Funcionario ? ((Funcionario) usuario).getTelefono() : "";
        panelCuenta.cargarDatos(nombre, usuario.getId(), telefono);

        JDialog dialogoCuenta = new JDialog(ventanaPropietaria, "Mi Perfil", true);
        dialogoCuenta.setUndecorated(true);
        dialogoCuenta.setResizable(false);
        dialogoCuenta.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());
        dialogoCuenta.setContentPane(panelCuenta.obtenerPanel());

        panelCuenta.alGuardar(() -> guardarMiCuenta(panelCuenta));
        panelCuenta.alCambiarContrasena(panelCuenta::mostrarVistaClave);
        panelCuenta.alConfirmarCambioClave(() -> confirmarCambioClave(panelCuenta, usuario.getId()));
        panelCuenta.alCerrarSesion(() -> {
            dialogoCuenta.dispose();
            cerrarSesionYVolverALogin();
        });
        panelCuenta.alCerrar(dialogoCuenta::dispose);

        dialogoCuenta.pack();
        dialogoCuenta.setLocationRelativeTo(ventanaPropietaria);
        dialogoCuenta.setVisible(true);
    }

    void guardarMiCuenta(PanelCuenta panelCuenta) {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (!(usuario instanceof Funcionario)) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.INFORMACION,
                    "Guardar cambios",
                    "No hay datos adicionales que guardar para una cuenta de administrador."
            );
            return;
        }
        String telefono = panelCuenta.obtenerTelefono();
        if (telefono == null || telefono.isBlank()) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo guardar", "Debe ingresar el telefono."
            );
            return;
        }
        telefono = telefono.trim();
        if (!telefono.matches("[0-9()+\\-\\s]{4,}")) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo guardar",
                    "El telefono solo puede contener numeros, espacios y los simbolos + ( ) -."
            );
            return;
        }

        try {
            Funcionario funcionario = (Funcionario) usuario;
            funcionario.setTelefono(telefono);
            funcionarioService.modificar(funcionario);
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Guardar cambios",
                    "Los cambios se guardaron correctamente."
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo guardar", excepcion.getMessage());
        }
    }

    private void confirmarCambioClave(PanelCuenta panelCuenta, String idUsuario) {
        try {
            autenticacionService.cambiarClave(
                    idUsuario,
                    panelCuenta.obtenerClaveActual(),
                    panelCuenta.obtenerClaveNueva()
            );
            panelCuenta.mostrarVistaDatos();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Cambiar contraseña",
                    "La contraseña se actualizó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña", excepcion.getMessage()
            );
        }
    }

    private void cerrarSesionYVolverALogin() {
        SesionControl.obtenerInstancia().cerrarSesion();

        if (ventanaPropietaria instanceof JFrame ventana) {
            Main.mostrarLogin(ventana);
        }
    }
}
