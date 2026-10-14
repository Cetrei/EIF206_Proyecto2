package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.ArrayList;

final class RutasCategoria {
    private RutasCategoria() {
    }

    static void registrar(Despachador despachador, CategoriaService servicio) {
        despachador.registrar(Accion.GET, Rutas.CATEGORIAS, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarTodas()));
        despachador.registrar(Accion.GET, Rutas.CATEGORIAS_BUSCAR, (cuerpo, sesion) ->
                new ArrayList<>(servicio.buscarPorDescripcion((String) cuerpo)));
        despachador.registrar(Accion.POST, Rutas.CATEGORIAS, (cuerpo, sesion) -> servicio.crear((String) cuerpo));
        despachador.registrar(Accion.PUT, Rutas.CATEGORIAS, (cuerpo, sesion) -> {
            servicio.modificar((Categoria) cuerpo);
            return null;
        });
        despachador.registrar(Accion.DELETE, Rutas.CATEGORIAS, (cuerpo, sesion) -> {
            servicio.eliminar((Integer) cuerpo);
            return null;
        });
    }
}
