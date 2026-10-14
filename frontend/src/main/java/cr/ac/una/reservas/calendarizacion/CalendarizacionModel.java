package cr.ac.una.reservas.calendarizacion;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.ui.AbstractModel;
import cr.ac.una.reservas.ui.componentes.MatrizFillStrategy;

import java.util.ArrayList;
import java.util.List;

public class CalendarizacionModel extends AbstractModel {

    public static final String PROP_CATEGORIAS_DISPONIBLES = "categoriasDisponibles";
    public static final String PROP_MATRIZ = "matriz";

    private List<Categoria> categoriasDisponibles = new ArrayList<>();
    private MatrizFillStrategy matriz;

    public List<Categoria> getCategoriasDisponibles() {
        return categoriasDisponibles;
    }

    public void setCategoriasDisponibles(List<Categoria> nuevaLista) {
        List<Categoria> anterior = this.categoriasDisponibles;
        this.categoriasDisponibles = nuevaLista;
        notificarCambioForzado(PROP_CATEGORIAS_DISPONIBLES, anterior, nuevaLista);
    }

    public MatrizFillStrategy getMatriz() {
        return matriz;
    }

    public void setMatriz(MatrizFillStrategy nuevaMatriz) {
        MatrizFillStrategy anterior = this.matriz;
        this.matriz = nuevaMatriz;
        notificarCambio(PROP_MATRIZ, anterior, nuevaMatriz);
    }
}
