package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.List;
import java.util.Optional;

public final class FuncionarioServiceProxy extends ProxyBase implements FuncionarioService {

    public FuncionarioServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public Optional<Funcionario> buscarPorId(String id) {
        return Optional.ofNullable((Funcionario) llamar(Accion.GET, Rutas.FUNCIONARIOS_POR_ID, id));
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        return lista(llamar(Accion.GET, Rutas.FUNCIONARIOS_BUSCAR, nombre));
    }

    @Override
    public List<Funcionario> listarTodos() {
        return lista(llamar(Accion.GET, Rutas.FUNCIONARIOS, null));
    }

    @Override
    public void crear(Funcionario funcionario) {
        llamar(Accion.POST, Rutas.FUNCIONARIOS, funcionario);
    }

    @Override
    public void modificar(Funcionario funcionario) {
        llamar(Accion.PUT, Rutas.FUNCIONARIOS, funcionario);
    }

    @Override
    public void eliminar(String id) {
        llamar(Accion.DELETE, Rutas.FUNCIONARIOS, id);
    }
}
