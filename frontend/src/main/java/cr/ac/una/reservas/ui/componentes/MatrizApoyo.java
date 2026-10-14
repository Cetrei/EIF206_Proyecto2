package cr.ac.una.reservas.ui.componentes;

import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

final class MatrizApoyo {
    private static final LocalTime HORA_INICIO = LocalTime.of(6, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(21, 0);

    private MatrizApoyo() {
    }

    static List<LocalTime> generarHoras() {
        List<LocalTime> horas = new ArrayList<>();
        LocalTime actual = HORA_INICIO;
        while (!actual.isAfter(HORA_FIN)) {
            horas.add(actual);
            actual = actual.plusHours(1);
        }
        return horas;
    }

    static JLabel crearMensajeSinColumnas(Tema tema) {
        JLabel mensaje = new JLabel(
                "Seleccione los filtros y presione \"Cargar\" para ver la disponibilidad.",
                SwingConstants.CENTER
        );
        mensaje.setForeground(tema.colorTextoSecundario());
        mensaje.setFont(tema.fuenteTexto());
        mensaje.setBorder(new EmptyBorder(24, 16, 24, 16));
        return mensaje;
    }

    static GridBagConstraints restriccionMensaje() {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1;
        restricciones.weighty = 1;
        restricciones.fill = GridBagConstraints.BOTH;
        return restricciones;
    }

    static GridBagConstraints restriccionColumnaHora(int fila) {
        GridBagConstraints restricciones = restriccionBase(fila, 0);
        restricciones.weightx = 0;
        return restricciones;
    }

    static GridBagConstraints restriccionColumnaDia(int fila, int columna) {
        GridBagConstraints restricciones = restriccionBase(fila, columna);
        restricciones.weightx = 1;
        return restricciones;
    }

    private static GridBagConstraints restriccionBase(int fila, int columna) {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = columna;
        restricciones.gridy = fila;
        restricciones.insets = new Insets(0, 0, 0, 0);
        restricciones.fill = GridBagConstraints.BOTH;
        return restricciones;
    }
}
