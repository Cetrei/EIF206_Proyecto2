package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.ui.iconos.Icono;
import cr.ac.una.reservas.ui.iconos.IconoAplicador;
import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;

final class TarjetaAlerta {
    private static final int ALTO = 42;

    private final JPanel panel = new JPanel(new BorderLayout(8, 0));

    TarjetaAlerta(AlertaReserva alerta, Tema tema, Runnable alVer, Runnable alDescartar) {
        String texto = alerta.getEncargado() + " - " + alerta.getActividad();
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setToolTipText(texto);
        etiqueta.setForeground(tema.colorTexto());
        etiqueta.setFont(tema.fuenteTexto().deriveFont(12f));
        etiqueta.setMinimumSize(new Dimension(0, ALTO));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acciones.setOpaque(false);
        acciones.add(crearBoton(Icono.INFO, tema.colorPrimario(), "Ver", alVer));
        acciones.add(crearBoton(Icono.CERRAR, tema.colorPeligro(), "Descartar", alDescartar));

        panel.setBackground(tema.colorFondoTarjeta());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(tema.colorBorde(), 1, true), new EmptyBorder(4, 8, 4, 6)
        ));
        panel.setPreferredSize(new Dimension(0, ALTO));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO));
        panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        panel.add(etiqueta, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.EAST);
    }

    JPanel obtenerPanel() {
        return panel;
    }

    private static JButton crearBoton(Icono icono, java.awt.Color color, String ayuda, Runnable accion) {
        JButton boton = new JButton();
        boton.setToolTipText(ayuda);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        IconoAplicador.aplicar(boton, icono, 15f, color);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }
}
