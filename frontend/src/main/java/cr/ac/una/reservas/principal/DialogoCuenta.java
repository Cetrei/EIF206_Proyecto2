package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.ui.componentes.PanelCuenta;

import javax.swing.JDialog;
import java.awt.Frame;

final class DialogoCuenta {
    private final Frame ventanaPropietaria;
    private final JDialog dialogo;

    DialogoCuenta(Frame ventanaPropietaria, PanelCuenta panelCuenta) {
        this.ventanaPropietaria = ventanaPropietaria;
        this.dialogo = new JDialog(ventanaPropietaria, "Mi Perfil", true);
        dialogo.setUndecorated(true);
        dialogo.setResizable(false);
        dialogo.setContentPane(panelCuenta.obtenerPanel());
    }

    void mostrar() {
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPropietaria);
        dialogo.setVisible(true);
    }

    void cerrar() {
        dialogo.dispose();
    }
}
