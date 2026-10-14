package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;
import cr.ac.una.reservas.reserva.DatosNuevaReserva;
import cr.ac.una.reservas.reserva.Reserva;
import cr.ac.una.reservas.reserva.ReservaObserver;
import cr.ac.una.reservas.reserva.ReservaService;
import cr.ac.una.reservas.reserva.ResultadoReserva;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ReservaServiceProxy extends ProxyBase implements ReservaService {
    private final List<ReservaObserver> observadores = new CopyOnWriteArrayList<>();

    public ReservaServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public List<Reserva> listarReservasDeFuncionario(String idFuncionario) {
        return lista(llamar(Accion.GET, Rutas.RESERVAS_DE_FUNCIONARIO, idFuncionario));
    }

    @Override
    public List<Reserva> listarTodasOrdenadas() {
        return lista(llamar(Accion.GET, Rutas.RESERVAS, null));
    }

    @Override
    public List<Reserva> listarReservasActivasEnFecha(LocalDate fecha) {
        return lista(llamar(Accion.GET, Rutas.RESERVAS_ACTIVAS, fecha));
    }

    @Override
    public ResultadoReserva intentarReservar(DatosNuevaReserva datos) {
        ResultadoReserva resultado = (ResultadoReserva) llamar(Accion.POST, Rutas.RESERVAS, datos);
        if (resultado.isExitoso()) {
            observadores.forEach(observador -> observador.onReservaCreada(resultado.getReserva()));
        }
        return resultado;
    }

    @Override
    public ResultadoReserva intentarModificar(DatosNuevaReserva datos) {
        ResultadoReserva resultado = (ResultadoReserva) llamar(Accion.PUT, Rutas.RESERVAS, datos);
        if (resultado.isExitoso()) {
            observadores.forEach(observador -> observador.onReservaModificada(resultado.getReserva()));
        }
        return resultado;
    }

    @Override
    public void cancelarReserva(Integer idReserva) {
        llamar(Accion.DELETE, Rutas.RESERVAS, idReserva);
        Reserva cancelada = new Reserva();
        cancelada.setId(idReserva);
        observadores.forEach(observador -> observador.onReservaCancelada(cancelada));
    }

    @Override
    public void agregarObservador(ReservaObserver observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(ReservaObserver observador) {
        observadores.remove(observador);
    }
}
