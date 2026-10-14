package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.categoria.Categoria;

import java.util.List;

public interface CategoriaDao extends GenericDAO<Categoria, Integer> {
    List<Categoria> buscarPorDescripcion(String texto);
}
