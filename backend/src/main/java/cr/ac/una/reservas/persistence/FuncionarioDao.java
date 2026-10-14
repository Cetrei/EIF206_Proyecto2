package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.funcionario.Funcionario;

import java.util.List;

public interface FuncionarioDao extends GenericDAO<Funcionario, String> {
    List<Funcionario> buscarPorNombre(String nombre);
}
