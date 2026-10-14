package cr.ac.una.reservas.autenticacion;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class LoginTarjeta extends JPanel {
    private static final int RADIO_BORDE = 16;

    LoginTarjeta() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        Graphics2D graficos2D = (Graphics2D) graficos.create();
        graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graficos2D.setColor(getBackground());
        graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
        graficos2D.dispose();
        super.paintComponent(graficos);
    }
}
