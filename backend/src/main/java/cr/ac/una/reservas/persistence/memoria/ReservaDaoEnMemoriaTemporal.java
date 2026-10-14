package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.reserva.Reserva;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporalNumerico<Reserva> implements ReservaDao {
    public ReservaDaoEnMemoriaTemporal() {
        super(Reserva::getId, Reserva::setId);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return listarTodos().stream()
                .filter(reserva -> idFuncionario != null && idFuncionario.equals(reserva.getIdFuncionario()))
                .collect(Collectors.toList());
    }
}
