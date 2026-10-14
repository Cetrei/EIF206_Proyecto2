package cr.ac.una.reservas.categoria;

import cr.ac.una.reservas.ui.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriaModel extends AbstractModel {

    public static final String PROP_CATEGORIAS = "categorias";
    public static final String PROP_CATEGORIA_SELECCIONADA = "categoriaSeleccionada";

    private List<Categoria> categorias = new ArrayList<>();
    private Categoria categoriaSeleccionada;

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> nuevaLista) {
        List<Categoria> anterior = this.categorias;
        this.categorias = nuevaLista;
        notificarCambioForzado(PROP_CATEGORIAS, anterior, nuevaLista);
    }

    public Categoria getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(Categoria nueva) {
        Categoria anterior = this.categoriaSeleccionada;
        this.categoriaSeleccionada = nueva;
        notificarCambio(PROP_CATEGORIA_SELECCIONADA, anterior, nueva);
    }
}
