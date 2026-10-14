package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.recurso.RecursoService;

import java.io.Serializable;
import java.util.ArrayList;

final class RutasRecurso {
    private RutasRecurso() {
    }

    static void registrar(Despachador despachador, RecursoService servicio) {
        despachador.registrar(Accion.GET, Rutas.RECURSOS, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarTodos()));
        despachador.registrar(Accion.GET, Rutas.RECURSOS_POR_CATEGORIA, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarPorCategoria((Integer) cuerpo)));
        despachador.registrar(Accion.GET, Rutas.RECURSOS_BUSCAR, (cuerpo, sesion) ->
                new ArrayList<>(servicio.buscarPorDescripcion((String) cuerpo)));
        despachador.registrar(Accion.GET, Rutas.RECURSOS_FILTRAR, (cuerpo, sesion) -> {
            Serializable[] argumentos = (Serializable[]) cuerpo;
            return new ArrayList<>(servicio.filtrar((Integer) argumentos[0], (String) argumentos[1]));
        });
        despachador.registrar(Accion.POST, Rutas.RECURSOS, (cuerpo, sesion) -> {
            servicio.crear((Recurso) cuerpo);
            return null;
        });
        despachador.registrar(Accion.PUT, Rutas.RECURSOS, (cuerpo, sesion) -> {
            servicio.modificar((Recurso) cuerpo);
            return null;
        });
        despachador.registrar(Accion.DELETE, Rutas.RECURSOS, (cuerpo, sesion) -> {
            servicio.eliminar((String) cuerpo);
            return null;
        });
    }
}
