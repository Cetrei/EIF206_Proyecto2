package cr.ac.una.reservas;

import cr.ac.una.reservas.autenticacion.LoginControl;
import cr.ac.una.reservas.autenticacion.LoginModel;
import cr.ac.una.reservas.autenticacion.LoginPanel;
import cr.ac.una.reservas.principal.ArranqueRemoto;
import cr.ac.una.reservas.principal.ManejadorExcepcionesGlobal;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.tema.GestorTema;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ManejadorExcepcionesGlobal.instalar();
        SwingUtilities.invokeLater(Main::iniciar);
    }

    public static void mostrarLogin(JFrame ventana) {
        LoginModel loginModelo = new LoginModel();
        LoginPanel loginPanel = new LoginPanel(loginModelo);
        new LoginControl(loginModelo, loginPanel, ventana);

        ventana.setContentPane(loginPanel.obtenerPanel());
        ventana.setMinimumSize(null);
        ventana.pack();
        ventana.setMinimumSize(ventana.getSize());
        ventana.setLocationRelativeTo(null);
        ventana.revalidate();
        ventana.repaint();
    }

    private static void iniciar() {
        GestorTema.aplicarUIManager(GestorTema.obtenerInstancia().temaActivo());

        JFrame ventana = new JFrame("Sistema de Reservas - EIF206 Proyecto 2");
        ventana.setUndecorated(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());

        if (!ServiceFactory.hayProveedor()) {
            ArranqueRemoto.iniciar(ventana, () -> mostrarLogin(ventana));
        }

        mostrarLogin(ventana);
        ventana.setVisible(true);
    }
}
