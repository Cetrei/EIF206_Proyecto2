package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.recurso.RecursoService;

import java.util.List;

public final class RecursoServiceProxy extends ProxyBase implements RecursoService {

    public RecursoServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public List<Recurso> listarPorCategoria(Integer idCategoria) {
        return lista(llamar(Accion.GET, Rutas.RECURSOS_POR_CATEGORIA, idCategoria));
    }

    @Override
    public List<Recurso> buscarPorDescripcion(String texto) {
        return lista(llamar(Accion.GET, Rutas.RECURSOS_BUSCAR, texto));
    }

    @Override
    public List<Recurso> listarTodos() {
        return lista(llamar(Accion.GET, Rutas.RECURSOS, null));
    }

    @Override
    public List<Recurso> filtrar(Integer idCategoria, String textoDescripcion) {
        return lista(llamar(Accion.GET, Rutas.RECURSOS_FILTRAR, argumentos(idCategoria, textoDescripcion)));
    }

    @Override
    public void crear(Recurso recurso) {
        llamar(Accion.POST, Rutas.RECURSOS, recurso);
    }

    @Override
    public void modificar(Recurso recurso) {
        llamar(Accion.PUT, Rutas.RECURSOS, recurso);
    }

    @Override
    public void eliminar(String id) {
        llamar(Accion.DELETE, Rutas.RECURSOS, id);
    }
}
