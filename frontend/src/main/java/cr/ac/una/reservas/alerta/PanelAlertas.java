package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.ui.iconos.Icono;
import cr.ac.una.reservas.ui.tema.CambioTemaListener;
import cr.ac.una.reservas.ui.tema.GestorTema;
import cr.ac.una.reservas.ui.tema.Tema;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class PanelAlertas implements CambioTemaListener {
    private static final int ANCHO = 300;
    private static final int ANCHO_ICONO = 46;
    private static final int COLUMNA_VER = 1;
    private static final int COLUMNA_DESCARTAR = 2;

    private JPanel pnlRaiz;
    private JLabel lblTitulo;
    private JPanel pnlTabla;
    private JTable tabla;
    private JScrollPane desplazamiento;
    private DefaultTableModel modeloTabla;

    private List<AlertaReserva> alertas = List.of();
    private Consumer<AlertaReserva> accionVer = alerta -> { };
    private Consumer<AlertaReserva> accionDescartar = alerta -> { };

    public PanelAlertas() {
        $$$setupUI$$$();
        lblTitulo.setText("Alertas");
        lblTitulo.setBorder(new EmptyBorder(10, 12, 6, 12));
        pnlRaiz.setPreferredSize(new Dimension(ANCHO, 0));
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                alPulsar(evento);
            }
        });
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"Actividad", "Ver", "Desc."}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(30);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setResizingAllowed(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tabla.getTableHeader().setDefaultRenderer(RenderizadoresAlerta.encabezado());
        tabla.getColumnModel().getColumn(0).setCellRenderer(RenderizadoresAlerta.texto());
        configurarIcono(COLUMNA_VER, RenderizadoresAlerta.icono(Icono.CARPETA, false, "Ver"));
        configurarIcono(COLUMNA_DESCARTAR, RenderizadoresAlerta.icono(Icono.CIRCULO_MENOS, true, "Descartar"));

        desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.add(desplazamiento, BorderLayout.CENTER);
    }

    private void configurarIcono(int columna, TableCellRenderer renderizador) {
        var modeloColumna = tabla.getColumnModel().getColumn(columna);
        modeloColumna.setCellRenderer(renderizador);
        modeloColumna.setPreferredWidth(ANCHO_ICONO);
        modeloColumna.setMinWidth(ANCHO_ICONO);
        modeloColumna.setResizable(true);
    }

    public void alVer(Consumer<AlertaReserva> accion) {
        this.accionVer = accion;
    }

    public void alDescartar(Consumer<AlertaReserva> accion) {
        this.accionDescartar = accion;
    }

    public void setAlertas(List<AlertaReserva> nuevasAlertas) {
        this.alertas = List.copyOf(nuevasAlertas);
        modeloTabla.setRowCount(0);
        for (AlertaReserva alerta : alertas) {
            modeloTabla.addRow(new Object[]{alerta.getEncargado() + " - " + alerta.getActividad(), "", ""});
        }
    }

    public JPanel obtenerPanel() {
        return pnlRaiz;
    }

    public JPanel envolver(JPanel contenido) {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(contenido, BorderLayout.CENTER);
        contenedor.add(pnlRaiz, BorderLayout.EAST);
        return contenedor;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void alPulsar(MouseEvent evento) {
        int fila = tabla.rowAtPoint(evento.getPoint());
        int columna = tabla.columnAtPoint(evento.getPoint());
        if (fila < 0 || fila >= alertas.size()) {
            return;
        }
        tabla.setRowSelectionInterval(fila, fila);
        AlertaReserva alerta = alertas.get(fila);
        if (columna == COLUMNA_VER) {
            accionVer.accept(alerta);
        } else if (columna == COLUMNA_DESCARTAR) {
            accionDescartar.accept(alerta);
        }
    }

    private void aplicarTema(Tema tema) {
        pnlRaiz.setBackground(tema.colorFondoVentana());
        pnlRaiz.setBorder(new MatteBorder(0, 1, 0, 0, tema.colorBorde()));
        lblTitulo.setForeground(tema.colorTexto());
        lblTitulo.setFont(tema.fuenteTitulo().deriveFont(Font.BOLD, 14f));
        pnlTabla.setBackground(tema.colorFondoVentana());
        tabla.setBackground(tema.colorFondoTarjeta());
        tabla.setForeground(tema.colorTexto());
        tabla.setSelectionBackground(tema.colorPrimario());
        tabla.setSelectionForeground(tema.colorTextoSobrePrimario());
        desplazamiento.getViewport().setBackground(tema.colorFondoTarjeta());
        tabla.getTableHeader().repaint();
        tabla.repaint();
    }

    /**
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        pnlRaiz = new JPanel();
        pnlRaiz.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, 0));
        lblTitulo = new JLabel();
        lblTitulo.setText("Alertas");
        pnlRaiz.add(lblTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlRaiz.add(pnlTabla, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pnlRaiz;
    }
}
