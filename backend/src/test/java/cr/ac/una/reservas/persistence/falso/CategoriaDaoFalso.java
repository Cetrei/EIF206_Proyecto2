package cr.ac.una.reservas.persistence.falso;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDaoFalso extends DaoFalsoNumerico<Categoria> implements CategoriaDao {
    public CategoriaDaoFalso() {
        super(Categoria::getId, Categoria::setId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return listarTodos().stream()
                .filter(categoria -> TextoBusqueda.contiene(categoria.getDescripcion(), texto))
                .collect(Collectors.toList());
    }
}
