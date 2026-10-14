package cr.ac.una.reservas.persistence.falso;

import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.reserva.Reserva;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaDaoFalso extends DaoFalsoNumerico<Reserva> implements ReservaDao {
    public ReservaDaoFalso() {
        super(Reserva::getId, Reserva::setId);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return listarTodos().stream()
                .filter(reserva -> idFuncionario.equals(reserva.getIdFuncionario()))
                .collect(Collectors.toList());
    }
}
