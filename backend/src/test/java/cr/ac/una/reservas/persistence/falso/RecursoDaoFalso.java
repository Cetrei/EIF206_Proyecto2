package cr.ac.una.reservas.persistence.falso;

import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.recurso.Recurso;

import java.util.List;
import java.util.stream.Collectors;

public class RecursoDaoFalso extends DaoFalso<Recurso, String> implements RecursoDao {
    public RecursoDaoFalso() {
        super(Recurso::getId);
    }

    @Override
    public List<Recurso> listarPorCategoria(Integer idCategoria) {
        return listarTodos().stream()
                .filter(recurso -> idCategoria.equals(recurso.getIdCategoria()))
                .collect(Collectors.toList());
    }
}
