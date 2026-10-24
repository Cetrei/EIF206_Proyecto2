package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.ui.iconos.FuenteIconos;
import cr.ac.una.reservas.ui.iconos.Icono;
import cr.ac.una.reservas.ui.tema.GestorTema;
import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Font;

final class RenderizadoresAlerta {
    private RenderizadoresAlerta() {
    }

    static TableCellRenderer texto() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tabla, Object valor, boolean seleccionada, boolean enfoque, int fila, int columna
            ) {
                super.getTableCellRendererComponent(tabla, valor, seleccionada, false, fila, columna);
                Tema tema = GestorTema.obtenerInstancia().temaActivo();
                setFont(tema.fuenteTexto().deriveFont(12f));
                setBorder(new EmptyBorder(0, 10, 0, 6));
                setToolTipText(valor == null ? null : valor.toString());
                return this;
            }
        };
    }

    static TableCellRenderer icono(Icono icono, boolean esPeligro, String ayuda) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tabla, Object valor, boolean seleccionada, boolean enfoque, int fila, int columna
            ) {
                super.getTableCellRendererComponent(tabla, "", seleccionada, false, fila, columna);
                Tema tema = GestorTema.obtenerInstancia().temaActivo();
                setText(FuenteIconos.obtenerTexto(icono));
                setFont(FuenteIconos.obtenerFuente(15f));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder());
                setToolTipText(ayuda);
                if (esPeligro) {
                    setForeground(tema.colorPeligro());
                } else {
                    setForeground(seleccionada ? tema.colorTextoSobrePrimario() : tema.colorPrimario());
                }
                return this;
            }
        };
    }

    static TableCellRenderer encabezado() {
        return (tabla, valor, seleccionada, enfoque, fila, columna) -> {
            Tema tema = GestorTema.obtenerInstancia().temaActivo();
            JLabel etiqueta = new JLabel(valor == null ? "" : valor.toString());
            etiqueta.setOpaque(true);
            etiqueta.setBackground(tema.colorSuperficie());
            etiqueta.setForeground(tema.colorTexto());
            etiqueta.setFont(tema.fuenteTitulo().deriveFont(Font.BOLD, 12f));
            etiqueta.setHorizontalAlignment(columna == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            etiqueta.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, tema.colorBorde()), new EmptyBorder(4, 10, 4, 6)
            ));
            return etiqueta;
        };
    }
}
