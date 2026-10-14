package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.ArrayList;

final class RutasFuncionario {
    private RutasFuncionario() {
    }

    static void registrar(Despachador despachador, FuncionarioService servicio) {
        despachador.registrar(Accion.GET, Rutas.FUNCIONARIOS, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarTodos()));
        despachador.registrar(Accion.GET, Rutas.FUNCIONARIOS_POR_ID, (cuerpo, sesion) ->
                servicio.buscarPorId((String) cuerpo).orElse(null));
        despachador.registrar(Accion.GET, Rutas.FUNCIONARIOS_BUSCAR, (cuerpo, sesion) ->
                new ArrayList<>(servicio.buscarPorNombre((String) cuerpo)));
        despachador.registrar(Accion.POST, Rutas.FUNCIONARIOS, (cuerpo, sesion) -> {
            servicio.crear((Funcionario) cuerpo);
            return null;
        });
        despachador.registrar(Accion.PUT, Rutas.FUNCIONARIOS, (cuerpo, sesion) -> {
            servicio.modificar((Funcionario) cuerpo);
            return null;
        });
        despachador.registrar(Accion.DELETE, Rutas.FUNCIONARIOS, (cuerpo, sesion) -> {
            servicio.eliminar((String) cuerpo);
            return null;
        });
    }
}
