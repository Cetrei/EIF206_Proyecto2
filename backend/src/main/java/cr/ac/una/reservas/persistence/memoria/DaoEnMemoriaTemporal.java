package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.persistence.GenericDAO;
import cr.ac.una.reservas.persistence.PersistenciaException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

class DaoEnMemoriaTemporal<T, ID> implements GenericDAO<T, ID> {
    private final Map<ID, T> almacen = new LinkedHashMap<>();
    private final Function<T, ID> extractorId;
    private final BiConsumer<T, ID> asignadorId;

    DaoEnMemoriaTemporal(Function<T, ID> extractorId) {
        this(extractorId, null);
    }

    DaoEnMemoriaTemporal(Function<T, ID> extractorId, BiConsumer<T, ID> asignadorId) {
        this.extractorId = extractorId;
        this.asignadorId = asignadorId;
    }

    protected ID generarId() {
        throw new PersistenciaException("La entidad no trae id");
    }

    @Override
    public synchronized Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public synchronized List<T> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public synchronized T insertar(T entidad) {
        ID id = extractorId.apply(entidad);
        if (id == null) {
            id = generarId();
            asignadorId.accept(entidad, id);
        }
        if (almacen.containsKey(id)) {
            throw new PersistenciaException("Ya existe un registro con el id " + id);
        }
        almacen.put(id, entidad);
        return entidad;
    }

    @Override
    public synchronized boolean actualizar(T entidad) {
        ID id = extractorId.apply(entidad);
        if (!almacen.containsKey(id)) {
            return false;
        }
        almacen.put(id, entidad);
        return true;
    }

    @Override
    public synchronized boolean eliminar(ID id) {
        return almacen.remove(id) != null;
    }
}
