package cr.ac.una.reservas.persistence.falso;

import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioDaoFalso extends DaoFalso<Funcionario, String> implements FuncionarioDao {
    public FuncionarioDaoFalso() {
        super(Funcionario::getId);
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        return listarTodos().stream()
                .filter(funcionario -> TextoBusqueda.contiene(funcionario.getNombre(), nombre))
                .collect(Collectors.toList());
    }
}
