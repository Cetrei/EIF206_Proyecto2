package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Funcionario, String> implements FuncionarioDao {
    public FuncionarioDaoEnMemoriaTemporal() {
        super(Funcionario::getId);
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        return listarTodos().stream()
                .filter(funcionario -> TextoBusqueda.contiene(funcionario.getNombre(), nombre))
                .collect(Collectors.toList());
    }
}
