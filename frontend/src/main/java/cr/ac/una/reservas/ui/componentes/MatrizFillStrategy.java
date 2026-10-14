package cr.ac.una.reservas.ui.componentes;

import java.util.List;

public interface MatrizFillStrategy {

    List<String> obtenerColumnas();

    DatosCelda obtenerCelda(int fila, int columna);

    void alHacerClickCelda(int fila, int columna, DatosCelda datos);
}
