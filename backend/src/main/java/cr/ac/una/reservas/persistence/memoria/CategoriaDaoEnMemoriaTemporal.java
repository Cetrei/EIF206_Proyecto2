package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporalNumerico<Categoria> implements CategoriaDao {
    public CategoriaDaoEnMemoriaTemporal() {
        super(Categoria::getId, Categoria::setId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return listarTodos().stream()
                .filter(categoria -> TextoBusqueda.contiene(categoria.getDescripcion(), texto))
                .collect(Collectors.toList());
    }
}
