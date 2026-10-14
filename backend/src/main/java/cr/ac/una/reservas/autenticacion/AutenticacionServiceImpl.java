package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.FuncionarioDao;

import java.util.Optional;

public class AutenticacionServiceImpl implements AutenticacionService {
    private final AdministradorDao administradorDao;
    private final FuncionarioDao funcionarioDao;

    public AutenticacionServiceImpl() {
        this(DaoFactory.obtenerAdministradorDao(), DaoFactory.obtenerFuncionarioDao());
    }

    public AutenticacionServiceImpl(AdministradorDao administradorDao, FuncionarioDao funcionarioDao) {
        this.administradorDao = administradorDao;
        this.funcionarioDao = funcionarioDao;
    }

    public Usuario autenticar(String id, String clave) {
        Optional<Usuario> usuario = buscarUsuarioPorId(id);
        if (usuario.isEmpty() || !usuario.get().getClave().equals(clave)) {
            throw new ReglaDeNegocioException("El ID o la contraseña son incorrectos.");
        }
        return usuario.get();
    }

    public void cambiarClave(String idUsuario, String claveActual, String claveNueva) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorId(idUsuario);
        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getClave().equals(claveActual)) {
            throw new ReglaDeNegocioException("La contraseña actual es incorrecta.");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new ReglaDeNegocioException("La nueva contraseña no puede estar vacía.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setClave(claveNueva);
        guardar(usuario);
    }

    private Optional<Usuario> buscarUsuarioPorId(String id) {
        Optional<Administrador> administrador = administradorDao.buscarPorId(id);
        if (administrador.isPresent()) {
            return Optional.of(administrador.get());
        }
        return funcionarioDao.buscarPorId(id).map(funcionario -> funcionario);
    }

    private void guardar(Usuario usuario) {
        if (usuario instanceof Administrador) {
            administradorDao.actualizar((Administrador) usuario);
        } else if (usuario instanceof Funcionario) {
            funcionarioDao.actualizar((Funcionario) usuario);
        }
    }
}
