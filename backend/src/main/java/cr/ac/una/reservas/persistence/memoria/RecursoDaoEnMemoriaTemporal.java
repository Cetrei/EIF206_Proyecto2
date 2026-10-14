package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.recurso.Recurso;

import java.util.List;
import java.util.stream.Collectors;

public class RecursoDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Recurso, String> implements RecursoDao {
    public RecursoDaoEnMemoriaTemporal() {
        super(Recurso::getId);
    }

    @Override
    public List<Recurso> listarPorCategoria(Integer idCategoria) {
        return listarTodos().stream()
                .filter(recurso -> idCategoria != null && idCategoria.equals(recurso.getIdCategoria()))
                .collect(Collectors.toList());
    }
}
