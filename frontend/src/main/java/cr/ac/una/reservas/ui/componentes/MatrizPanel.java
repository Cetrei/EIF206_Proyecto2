package cr.ac.una.reservas.ui.componentes;

import cr.ac.una.reservas.ui.tema.CambioTemaListener;
import cr.ac.una.reservas.ui.tema.GestorTema;
import cr.ac.una.reservas.ui.tema.Tema;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MatrizPanel implements CambioTemaListener {
    public static LocalTime obtenerHoraDeFila(int fila) {
        List<LocalTime> horas = MatrizApoyo.generarHoras();
        if (fila < 0 || fila >= horas.size()) return null;
        return horas.get(fila);
    }

    private JPanel MatrizPanel;
    private JScrollPane scrollMatriz;
    private JPanel pnlGrilla;

    private MatrizFillStrategy estrategia;
    private final List<CeldaMatriz> celdasActivas = new ArrayList<>();
    private final List<JLabel> etiquetasEncabezado = new ArrayList<>();
    private final List<JLabel> etiquetasHora = new ArrayList<>();
    private final List<JLabel> etiquetasCeldaVacia = new ArrayList<>();
    private Tema temaActual;

    public MatrizPanel() {
        $$$setupUI$$$();
        GestorTema.obtenerInstancia().agregarListener(this);
        temaActual = GestorTema.obtenerInstancia().temaActivo();
        scrollMatriz.setPreferredSize(new Dimension(0, 320));
        ScrollWheelPassthrough.instalar(scrollMatriz);
        scrollMatriz.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evento) {
                pnlGrilla.revalidate();
            }
        });
        aplicarTema(temaActual);
    }

    private void createUIComponents() {
        pnlGrilla = new MatrizGrilla();
    }

    public void setEstrategia(MatrizFillStrategy estrategia) {
        this.estrategia = estrategia;
        redibujar();
    }

    public void redibujar() {
        pnlGrilla.removeAll();
        celdasActivas.clear();
        etiquetasEncabezado.clear();
        etiquetasHora.clear();
        etiquetasCeldaVacia.clear();
        if (estrategia == null) {
            pnlGrilla.revalidate();
            pnlGrilla.repaint();
            return;
        }

        List<String> columnas = estrategia.obtenerColumnas();
        if (columnas.isEmpty()) {
            mostrarMensajeSinColumnas();
            pnlGrilla.revalidate();
            pnlGrilla.repaint();
            return;
        }
        List<LocalTime> horas = MatrizApoyo.generarHoras();

        agregarCabeceras(columnas);
        for (int fila = 0; fila < horas.size(); fila++) {
            agregarCabeceraHora(horas.get(fila), fila);
            for (int columna = 0; columna < columnas.size(); columna++) {
                agregarCelda(fila, columna);
            }
        }

        aplicarTemaAEtiquetas(temaActual);
        pnlGrilla.revalidate();
        pnlGrilla.repaint();
    }

    private void mostrarMensajeSinColumnas() {
        pnlGrilla.add(MatrizApoyo.crearMensajeSinColumnas(temaActual), MatrizApoyo.restriccionMensaje());
    }

    private void agregarCabeceras(List<String> columnas) {
        JLabel esquina = new JLabel("Hora", SwingConstants.CENTER);
        etiquetasEncabezado.add(esquina);
        pnlGrilla.add(esquina, MatrizApoyo.restriccionColumnaHora(0));
        for (int columna = 0; columna < columnas.size(); columna++) {
            JLabel etiqueta = new JLabel(columnas.get(columna), SwingConstants.CENTER);
            etiquetasEncabezado.add(etiqueta);
            pnlGrilla.add(etiqueta, MatrizApoyo.restriccionColumnaDia(0, columna + 1));
        }
    }

    private void agregarCabeceraHora(LocalTime hora, int fila) {
        JLabel etiqueta = new JLabel(hora.toString(), SwingConstants.CENTER);
        etiquetasHora.add(etiqueta);
        pnlGrilla.add(etiqueta, MatrizApoyo.restriccionColumnaHora(fila + 1));
    }

    private void agregarCelda(int fila, int columna) {
        DatosCelda datos = estrategia.obtenerCelda(fila, columna);
        GridBagConstraints restricciones = MatrizApoyo.restriccionColumnaDia(fila + 1, columna + 1);
        if (datos == null) {
            JLabel etiquetaVacia = new JLabel("-", SwingConstants.CENTER);
            etiquetasCeldaVacia.add(etiquetaVacia);
            pnlGrilla.add(etiquetaVacia, restricciones);
            return;
        }
        restricciones.insets = new Insets(3, 3, 3, 3);
        CeldaMatriz celda = new CeldaMatriz();
        celda.setDatos(datos);
        int filaFinal = fila;
        int columnaFinal = columna;
        celda.obtenerPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                estrategia.alHacerClickCelda(filaFinal, columnaFinal, datos);
            }
        });
        celdasActivas.add(celda);
        pnlGrilla.add(celda.obtenerPanel(), restricciones);
    }

    public JPanel obtenerPanel() {
        return MatrizPanel;
    }

    @Override
    public void onCambioTema(Tema tema) {
        temaActual = tema;
        aplicarTema(tema);
        aplicarTemaAEtiquetas(tema);
    }

    private void aplicarTema(Tema tema) {
        pnlGrilla.setBackground(tema.colorFondoTarjeta());
        MatrizPanel.setBackground(tema.colorFondoTarjeta());
        scrollMatriz.getViewport().setBackground(tema.colorFondoTarjeta());
        scrollMatriz.setBackground(tema.colorFondoTarjeta());
        scrollMatriz.setViewportBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1));
        scrollMatriz.setBorder(BorderFactory.createEmptyBorder());
        ScrollBarTematizado.aplicar(scrollMatriz.getVerticalScrollBar(), tema);
        ScrollBarTematizado.aplicar(scrollMatriz.getHorizontalScrollBar(), tema);
        pnlGrilla.repaint();
    }

    private void aplicarTemaAEtiquetas(Tema tema) {
        int cantidadColumnas = estrategia == null ? 0 : estrategia.obtenerColumnas().size();
        MatrizEstiloEtiquetas.aplicar(tema, etiquetasEncabezado, etiquetasHora, etiquetasCeldaVacia, cantidadColumnas);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        MatrizPanel = new JPanel();
        MatrizPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollMatriz = new JScrollPane();
        MatrizPanel.add(scrollMatriz, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollMatriz.setViewportView(pnlGrilla);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MatrizPanel;
    }

}
