package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;
import cr.ac.una.reservas.reserva.DatosNuevaReserva;
import cr.ac.una.reservas.reserva.ReservaService;

import java.time.LocalDate;
import java.util.ArrayList;

final class RutasReserva {
    private RutasReserva() {
    }

    static void registrar(Despachador despachador, ReservaService servicio) {
        despachador.registrar(Accion.GET, Rutas.RESERVAS, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarTodasOrdenadas()));
        despachador.registrar(Accion.GET, Rutas.RESERVAS_DE_FUNCIONARIO, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarReservasDeFuncionario((String) cuerpo)));
        despachador.registrar(Accion.GET, Rutas.RESERVAS_ACTIVAS, (cuerpo, sesion) ->
                new ArrayList<>(servicio.listarReservasActivasEnFecha((LocalDate) cuerpo)));
        despachador.registrar(Accion.POST, Rutas.RESERVAS, (cuerpo, sesion) ->
                servicio.intentarReservar((DatosNuevaReserva) cuerpo));
        despachador.registrar(Accion.PUT, Rutas.RESERVAS, (cuerpo, sesion) ->
                servicio.intentarModificar((DatosNuevaReserva) cuerpo));
        despachador.registrar(Accion.DELETE, Rutas.RESERVAS, (cuerpo, sesion) -> {
            servicio.cancelarReserva((Integer) cuerpo);
            return null;
        });
    }
}
