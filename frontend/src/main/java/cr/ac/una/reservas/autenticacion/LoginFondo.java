package cr.ac.una.reservas.autenticacion;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class LoginFondo extends JPanel {
    @Override
    protected void paintComponent(Graphics graficos) {
        Graphics2D graficos2D = (Graphics2D) graficos.create();
        graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graficos2D.setColor(getBackground());
        graficos2D.fillRect(0, 0, getWidth(), getHeight());
        graficos2D.dispose();
        super.paintComponent(graficos);
    }
}
