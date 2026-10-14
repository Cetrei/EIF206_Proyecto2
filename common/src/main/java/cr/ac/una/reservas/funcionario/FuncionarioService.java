package cr.ac.una.reservas.funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioService {

    Optional<Funcionario> buscarPorId(String id);
    List<Funcionario> buscarPorNombre(String nombre);
    List<Funcionario> listarTodos();
    void crear(Funcionario funcionario);
    void modificar(Funcionario funcionario);
    void eliminar(String id);
}
