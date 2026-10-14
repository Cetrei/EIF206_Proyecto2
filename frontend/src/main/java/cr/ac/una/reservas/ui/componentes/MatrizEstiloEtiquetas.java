package cr.ac.una.reservas.ui.componentes;

import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

final class MatrizEstiloEtiquetas {
    private MatrizEstiloEtiquetas() {
    }

    static void aplicar(
            Tema tema,
            List<JLabel> etiquetasEncabezado,
            List<JLabel> etiquetasHora,
            List<JLabel> etiquetasCeldaVacia,
            int cantidadColumnas
    ) {
        aplicarEncabezados(tema, etiquetasEncabezado);
        aplicarHoras(tema, etiquetasHora);
        aplicarCeldasVacias(tema, etiquetasCeldaVacia, Math.max(cantidadColumnas, 1));
    }

    private static void aplicarEncabezados(Tema tema, List<JLabel> etiquetas) {
        MatteBorder bordeEncabezado = BorderFactory.createMatteBorder(0, 0, 2, 1, tema.colorBorde());
        for (int i = 0; i < etiquetas.size(); i++) {
            JLabel etiqueta = etiquetas.get(i);
            etiqueta.setOpaque(true);
            etiqueta.setBackground(tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto().deriveFont(Font.BOLD));
            etiqueta.setBorder(new CompoundBorder(bordeEncabezado, new EmptyBorder(8, 6, 8, 6)));
            if (i > 0) {
                etiqueta.setMinimumSize(new Dimension(0, etiqueta.getPreferredSize().height));
            }
        }
    }

    private static void aplicarHoras(Tema tema, List<JLabel> etiquetas) {
        MatteBorder bordeHora = BorderFactory.createMatteBorder(0, 0, 1, 1, tema.colorBorde());
        for (JLabel etiqueta : etiquetas) {
            etiqueta.setOpaque(true);
            etiqueta.setBackground(tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto());
            etiqueta.setBorder(new CompoundBorder(bordeHora, new EmptyBorder(6, 10, 6, 10)));
        }
    }

    private static void aplicarCeldasVacias(Tema tema, List<JLabel> etiquetas, int cantidadColumnas) {
        MatteBorder bordeCelda = BorderFactory.createMatteBorder(0, 0, 1, 1, tema.colorBorde());
        for (int i = 0; i < etiquetas.size(); i++) {
            JLabel etiqueta = etiquetas.get(i);
            int fila = i / cantidadColumnas;
            etiqueta.setOpaque(true);
            etiqueta.setBackground(fila % 2 == 0 ? tema.colorFondoTarjeta() : tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto());
            etiqueta.setBorder(new CompoundBorder(bordeCelda, new EmptyBorder(6, 6, 6, 6)));
            etiqueta.setMinimumSize(new Dimension(0, etiqueta.getPreferredSize().height));
        }
    }
}
