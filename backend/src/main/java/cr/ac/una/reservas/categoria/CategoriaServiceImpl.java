package cr.ac.una.reservas.categoria;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.RecursoDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaDao categoriaDao;
    private final RecursoDao recursoDao;
    private final List<CategoriaObserver> observadores = new ArrayList<>();

    public CategoriaServiceImpl() {
        this(DaoFactory.obtenerCategoriaDao(), DaoFactory.obtenerRecursoDao());
    }

    public CategoriaServiceImpl(CategoriaDao categoriaDao, RecursoDao recursoDao) {
        this.categoriaDao = categoriaDao;
        this.recursoDao = recursoDao;
    }

    public List<Categoria> buscarPorDescripcion(String texto) {
        return categoriaDao.buscarPorDescripcion(texto);
    }

    public List<Categoria> listarTodas() {
        return categoriaDao.listarTodos();
    }

    public Categoria crear(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaDeNegocioException("La descripción de la categoría no puede estar vacía.");
        }
        Categoria categoria = categoriaDao.insertar(new Categoria(null, descripcion));
        notificarCategoriasCambiaron();
        return categoria;
    }

    public void modificar(Categoria categoria) {
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new ReglaDeNegocioException("La descripción de la categoría no puede estar vacía.");
        }
        if (categoriaDao.buscarPorId(categoria.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe una categoría con ese ID.");
        }
        categoriaDao.actualizar(categoria);
        notificarCategoriasCambiaron();
    }

    public void eliminar(Integer id) {
        if (categoriaDao.buscarPorId(id).isEmpty()) {
            throw new ReglaDeNegocioException("No existe una categoría con ese ID.");
        }
        if (!recursoDao.listarPorCategoria(id).isEmpty()) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar la categoría porque tiene recursos asociados."
            );
        }
        categoriaDao.eliminar(id);
        notificarCategoriasCambiaron();
    }

    public void agregarObservador(CategoriaObserver observador) {
        observadores.add(observador);
    }

    public void quitarObservador(CategoriaObserver observador) {
        observadores.remove(observador);
    }

    private void notificarCategoriasCambiaron() {
        for (CategoriaObserver observador : observadores) {
            observador.onCategoriasCambiaron();
        }
    }

    Optional<Categoria> buscarPorId(Integer id) {
        return categoriaDao.buscarPorId(id);
    }
}
