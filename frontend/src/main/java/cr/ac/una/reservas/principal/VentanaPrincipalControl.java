package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.alerta.AlertasControl;
import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Frame;

public class VentanaPrincipalControl {
    private final VentanaPrincipalModel modelo;
    private final VentanaPrincipal vista;
    private final Frame ventanaPropietaria;
    private final CuentaControl cuentaControl;

    public VentanaPrincipalControl(VentanaPrincipalModel modelo, VentanaPrincipal vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerAutenticacionService(),
                ServiceFactory.obtenerFuncionarioService()
        );
    }

    public VentanaPrincipalControl(
            VentanaPrincipalModel modelo,
            VentanaPrincipal vista,
            Frame ventanaPropietaria,
            AutenticacionService autenticacionService,
            FuncionarioService funcionarioService
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.cuentaControl = new CuentaControl(ventanaPropietaria, autenticacionService, funcionarioService);
        inicializarEncabezado();
        new PestanasPrincipalControl(vista, ventanaPropietaria).inicializar();
        this.vista.alAbrirCuenta(cuentaControl::abrirMiCuenta);
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

    private void inicializarEncabezado() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) return;

        String nombre = usuario instanceof Funcionario ? ((Funcionario) usuario).getNombre() : usuario.getId();
        String rolTexto = SesionControl.obtenerInstancia().esAdministrador() ? "ADMINISTRADOR" : "FUNCIONARIO";
        modelo.setUsuario(nombre, rolTexto);

        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            vista.mostrarSoloPestanasDeFuncionario();
        } else {
            vista.mostrarSoloPestanasDeAdministrador();
        }
    }

    public static void mostrarEn(JFrame ventana) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipalModel modelo = new VentanaPrincipalModel();
            VentanaPrincipal vista = new VentanaPrincipal(modelo);
            new VentanaPrincipalControl(modelo, vista, ventana);

            ventana.setContentPane(AlertasControl.envolver(ventana, vista.obtenerPanel()));
            ventana.setMinimumSize(null);
            ventana.pack();
            ventana.setMinimumSize(ventana.getSize());
            ventana.setLocationRelativeTo(null);
            ventana.revalidate();
            ventana.repaint();
        });
    }
}
