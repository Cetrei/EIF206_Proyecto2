package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.categoria.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class RecursoFiltroReporte {
    private RecursoFiltroReporte() {
    }

    static Map<String, String> metadatos(Categoria categoriaFiltro, String textoBusqueda) {
        List<String> partes = new ArrayList<>();
        if (categoriaFiltro != null) {
            partes.add("Categoria: " + categoriaFiltro.getDescripcion());
        }
        if (textoBusqueda != null && !textoBusqueda.isBlank()) {
            partes.add("Descripcion: " + textoBusqueda);
        }
        if (partes.isEmpty()) {
            return null;
        }
        return Map.of("subtitulo", "Filtrado por " + String.join(", ", partes));
    }
}
