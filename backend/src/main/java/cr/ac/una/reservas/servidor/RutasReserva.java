package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.excepcion.AutorizacionException;
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
                servicio.intentarReservar(conFuncionarioDeSesion((DatosNuevaReserva) cuerpo, sesion)));
        despachador.registrar(Accion.PUT, Rutas.RESERVAS, (cuerpo, sesion) ->
                servicio.intentarModificar(conFuncionarioDeSesion((DatosNuevaReserva) cuerpo, sesion)));
        despachador.registrar(Accion.DELETE, Rutas.RESERVAS, (cuerpo, sesion) -> {
            Integer idReserva = (Integer) cuerpo;
            if (!sesion.esAdministrador()) {
                exigirDuenio(servicio, idReserva, sesion);
            }
            servicio.cancelarReserva(idReserva);
            return null;
        });
    }

    private static DatosNuevaReserva conFuncionarioDeSesion(DatosNuevaReserva datos, SesionCliente sesion) {
        datos.setIdFuncionario(sesion.getUsuario().getId());
        return datos;
    }

    private static void exigirDuenio(ReservaService servicio, Integer idReserva, SesionCliente sesion) {
        boolean ajena = servicio.listarTodasOrdenadas().stream()
                .anyMatch(reserva -> idReserva.equals(reserva.getId())
                        && !sesion.getUsuario().getId().equals(reserva.getIdFuncionario()));
        if (ajena) {
            throw new AutorizacionException("Solo puede cancelar sus propias reservas.");
        }
    }
}
