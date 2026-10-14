package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaObserver;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CategoriaServiceProxy extends ProxyBase implements CategoriaService {
    private final List<CategoriaObserver> observadores = new CopyOnWriteArrayList<>();

    public CategoriaServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return lista(llamar(Accion.GET, Rutas.CATEGORIAS_BUSCAR, texto));
    }

    @Override
    public List<Categoria> listarTodas() {
        return lista(llamar(Accion.GET, Rutas.CATEGORIAS, null));
    }

    @Override
    public Categoria crear(String descripcion) {
        Categoria creada = (Categoria) llamar(Accion.POST, Rutas.CATEGORIAS, descripcion);
        notificarCambio();
        return creada;
    }

    @Override
    public void modificar(Categoria categoria) {
        llamar(Accion.PUT, Rutas.CATEGORIAS, categoria);
        notificarCambio();
    }

    @Override
    public void eliminar(Integer id) {
        llamar(Accion.DELETE, Rutas.CATEGORIAS, id);
        notificarCambio();
    }

    @Override
    public void agregarObservador(CategoriaObserver observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(CategoriaObserver observador) {
        observadores.remove(observador);
    }

    private void notificarCambio() {
        for (CategoriaObserver observador : observadores) {
            observador.onCategoriasCambiaron();
        }
    }
}
