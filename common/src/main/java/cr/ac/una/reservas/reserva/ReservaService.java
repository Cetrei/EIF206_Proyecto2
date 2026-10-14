package cr.ac.una.reservas.reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

    List<Reserva> listarReservasDeFuncionario(String idFuncionario);
    List<Reserva> listarTodasOrdenadas();
    List<Reserva> listarReservasActivasEnFecha(LocalDate fecha);
    ResultadoReserva intentarReservar(DatosNuevaReserva datos);
    ResultadoReserva intentarModificar(DatosNuevaReserva datos);
    void cancelarReserva(Integer idReserva);
    void agregarObservador(ReservaObserver observador);
    void quitarObservador(ReservaObserver observador);
}
