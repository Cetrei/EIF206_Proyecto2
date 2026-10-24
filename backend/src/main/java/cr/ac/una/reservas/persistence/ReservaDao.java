package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.reserva.Reserva;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface ReservaDao extends GenericDAO<Reserva, Integer> {
    List<Reserva> listarPorFuncionario(String idFuncionario);

    default Map<String, Long> contarRecursosReservados(LocalDate desde, LocalDate hasta) {
        Map<String, Long> conteo = new TreeMap<>();
        for (Reserva reserva : listarTodos()) {
            if (cuenta(reserva, desde, hasta)) {
                reserva.getIdsRecursosAsignados().forEach(idRecurso -> conteo.merge(idRecurso, 1L, Long::sum));
            }
        }
        return conteo;
    }

    default Map<LocalDate, Long> contarActividadesPorSemana(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Long> conteo = new TreeMap<>();
        for (Reserva reserva : listarTodos()) {
            if (cuenta(reserva, desde, hasta)) {
                LocalDate lunes = reserva.getFecha().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                conteo.merge(lunes, 1L, Long::sum);
            }
        }
        return conteo;
    }

    private static boolean cuenta(Reserva reserva, LocalDate desde, LocalDate hasta) {
        return reserva.getEstado() == EstadoReserva.ACTIVA
                && reserva.getFecha() != null
                && !reserva.getFecha().isBefore(desde)
                && !reserva.getFecha().isAfter(hasta);
    }
}
