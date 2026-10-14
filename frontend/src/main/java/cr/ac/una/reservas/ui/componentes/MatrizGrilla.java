package cr.ac.una.reservas.ui.componentes;

import javax.swing.JPanel;
import javax.swing.JViewport;
import java.awt.Dimension;
import java.awt.GridBagLayout;

final class MatrizGrilla extends JPanel {
    MatrizGrilla() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferido = super.getPreferredSize();
        if (getParent() instanceof JViewport) {
            int anchoViewport = getParent().getWidth();
            if (anchoViewport > 0) {
                return new Dimension(anchoViewport, preferido.height);
            }
        }
        return preferido;
    }
}
