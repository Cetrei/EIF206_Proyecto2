package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class RecursoServiceImpl implements RecursoService {
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;
    private final ReservaDao reservaDao;

    public RecursoServiceImpl() {
        this(DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao(), DaoFactory.obtenerReservaDao());
    }

    public RecursoServiceImpl(RecursoDao recursoDao, CategoriaDao categoriaDao, ReservaDao reservaDao) {
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
        this.reservaDao = reservaDao;
    }

    public List<Recurso> listarPorCategoria(Integer idCategoria) {
        return resolverCategorias(recursoDao.listarPorCategoria(idCategoria));
    }

    public List<Recurso> buscarPorDescripcion(String texto) {
        List<Recurso> recursos = recursoDao.listarTodos().stream()
                .filter(recurso -> TextoBusqueda.contiene(recurso.getDescripcion(), texto))
                .collect(Collectors.toList());
        return resolverCategorias(recursos);
    }

    public List<Recurso> listarTodos() {
        return resolverCategorias(recursoDao.listarTodos());
    }

    public List<Recurso> filtrar(Integer idCategoria, String textoDescripcion) {
        boolean filtrarPorCategoria = idCategoria != null;
        List<Recurso> recursos = recursoDao.listarTodos().stream()
                .filter(recurso -> !filtrarPorCategoria || idCategoria.equals(recurso.getIdCategoria()))
                .filter(recurso -> TextoBusqueda.contiene(recurso.getDescripcion(), textoDescripcion)
                        || TextoBusqueda.contiene(recurso.getId(), textoDescripcion))
                .collect(Collectors.toList());
        return resolverCategorias(recursos);
    }

    public void crear(Recurso recurso) {
        if (recurso.getId() == null || recurso.getId().isBlank()) {
            throw new ReglaDeNegocioException("El recurso debe tener un ID (número de activo).");
        }
        if (recursoDao.buscarPorId(recurso.getId()).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe un recurso con ese ID.");
        }
        validarCategoriaExiste(recurso.getIdCategoria());
        recursoDao.insertar(recurso);
    }

    public void modificar(Recurso recurso) {
        if (recursoDao.buscarPorId(recurso.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe un recurso con ese ID.");
        }
        validarCategoriaExiste(recurso.getIdCategoria());
        recursoDao.actualizar(recurso);
    }

    public void eliminar(String id) {
        if (recursoDao.buscarPorId(id).isEmpty()) {
            throw new ReglaDeNegocioException("No existe un recurso con ese ID.");
        }
        boolean asignadoAReservaActiva = reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .anyMatch(reserva -> reserva.getIdsRecursosAsignados().contains(id));
        if (asignadoAReservaActiva) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar el recurso porque está asignado a una reserva activa."
            );
        }
        recursoDao.eliminar(id);
    }

    private void validarCategoriaExiste(Integer idCategoria) {
        if (idCategoria == null || categoriaDao.buscarPorId(idCategoria).isEmpty()) {
            throw new ReglaDeNegocioException("Debe seleccionar una categoría válida para el recurso.");
        }
    }

    private List<Recurso> resolverCategorias(List<Recurso> recursos) {
        for (Recurso recurso : recursos) {
            categoriaDao.buscarPorId(recurso.getIdCategoria()).ifPresent(recurso::setCategoria);
        }
        return recursos;
    }
}
