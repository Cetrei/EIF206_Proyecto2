package cr.ac.una.reservas.persistence;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    T insertar(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(ID id);
}
