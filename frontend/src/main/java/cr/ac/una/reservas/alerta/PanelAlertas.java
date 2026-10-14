package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.ui.tema.CambioTemaListener;
import cr.ac.una.reservas.ui.tema.GestorTema;
import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;

public class PanelAlertas implements CambioTemaListener {
    private static final int ANCHO = 270;

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel titulo = new JLabel("Alertas");
    private final JPanel lista = new JPanel();
    private final JScrollPane desplazamiento = new JScrollPane(lista);

    private List<AlertaReserva> alertas = List.of();
    private Consumer<AlertaReserva> accionVer = alerta -> { };
    private Consumer<AlertaReserva> accionDescartar = alerta -> { };

    public PanelAlertas() {
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        titulo.setBorder(new EmptyBorder(10, 12, 10, 12));
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(desplazamiento, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(ANCHO, 0));
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public void alVer(Consumer<AlertaReserva> accion) {
        this.accionVer = accion;
    }

    public void alDescartar(Consumer<AlertaReserva> accion) {
        this.accionDescartar = accion;
    }

    public void setAlertas(List<AlertaReserva> nuevasAlertas) {
        this.alertas = List.copyOf(nuevasAlertas);
        reconstruir(GestorTema.obtenerInstancia().temaActivo());
    }

    public JPanel obtenerPanel() {
        return panel;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        panel.setBackground(tema.colorFondoVentana());
        panel.setBorder(new MatteBorder(0, 1, 0, 0, tema.colorBorde()));
        titulo.setForeground(tema.colorTexto());
        titulo.setFont(tema.fuenteTitulo().deriveFont(Font.BOLD, 14f));
        lista.setBackground(tema.colorFondoVentana());
        desplazamiento.getViewport().setBackground(tema.colorFondoVentana());
        reconstruir(tema);
    }

    private void reconstruir(Tema tema) {
        lista.removeAll();
        lista.setBorder(new EmptyBorder(0, 10, 10, 10));
        for (AlertaReserva alerta : alertas) {
            TarjetaAlerta tarjeta = new TarjetaAlerta(
                    alerta, tema, () -> accionVer.accept(alerta), () -> accionDescartar.accept(alerta)
            );
            lista.add(tarjeta.obtenerPanel());
            lista.add(Box.createVerticalStrut(6));
        }
        lista.add(Box.createVerticalGlue());
        lista.revalidate();
        lista.repaint();
    }
}
