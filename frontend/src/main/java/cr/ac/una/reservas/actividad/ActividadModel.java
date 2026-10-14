package cr.ac.una.reservas.actividad;

import cr.ac.una.reservas.ui.AbstractModel;
import cr.ac.una.reservas.ui.componentes.MatrizFillStrategy;

public class ActividadModel extends AbstractModel {

    public static final String PROP_MATRIZ = "matriz";

    private MatrizFillStrategy matriz;

    public MatrizFillStrategy getMatriz() {
        return matriz;
    }

    public void setMatriz(MatrizFillStrategy nuevaMatriz) {
        MatrizFillStrategy anterior = this.matriz;
        this.matriz = nuevaMatriz;
        notificarCambio(PROP_MATRIZ, anterior, nuevaMatriz);
    }
}
