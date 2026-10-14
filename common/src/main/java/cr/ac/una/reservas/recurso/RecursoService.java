package cr.ac.una.reservas.recurso;

import java.util.List;

public interface RecursoService {

    List<Recurso> listarPorCategoria(Integer idCategoria);
    List<Recurso> buscarPorDescripcion(String texto);
    List<Recurso> listarTodos();
    List<Recurso> filtrar(Integer idCategoria, String textoDescripcion);
    void crear(Recurso recurso);
    void modificar(Recurso recurso);
    void eliminar(String id);
}
