package cr.ac.una.reservas.categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> buscarPorDescripcion(String texto);
    List<Categoria> listarTodas();
    Categoria crear(String descripcion);
    void modificar(Categoria categoria);
    void eliminar(Integer id);
    void agregarObservador(CategoriaObserver observador);
    void quitarObservador(CategoriaObserver observador);
}
