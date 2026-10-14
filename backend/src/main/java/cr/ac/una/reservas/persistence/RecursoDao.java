package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.recurso.Recurso;

import java.util.List;

public interface RecursoDao extends GenericDAO<Recurso, String> {
    List<Recurso> listarPorCategoria(Integer idCategoria);
}
