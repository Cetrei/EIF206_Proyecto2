package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.ui.componentes.ScrollBarTematizado;
import cr.ac.una.reservas.ui.tema.GestorTema;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

final class ContenidoPestana {
    private ContenidoPestana() {
    }

    static void colocar(JPanel panelPestana, JPanel contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        ScrollBarTematizado.aplicar(scroll.getVerticalScrollBar(), GestorTema.obtenerInstancia().temaActivo());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelPestana.setLayout(new BorderLayout());
        panelPestana.setBorder(BorderFactory.createEmptyBorder());
        panelPestana.add(scroll, BorderLayout.CENTER);
    }
}
