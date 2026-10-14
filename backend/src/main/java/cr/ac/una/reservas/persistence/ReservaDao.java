package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.reserva.Reserva;

import java.util.List;

public interface ReservaDao extends GenericDAO<Reserva, Integer> {
    List<Reserva> listarPorFuncionario(String idFuncionario);
}
