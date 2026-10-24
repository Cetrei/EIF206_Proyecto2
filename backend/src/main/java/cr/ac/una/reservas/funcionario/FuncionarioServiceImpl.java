package cr.ac.una.reservas.funcionario;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.reserva.EstadoReserva;

import java.util.List;
import java.util.Optional;

public class FuncionarioServiceImpl implements FuncionarioService {
    private final FuncionarioDao funcionarioDao;
    private final ReservaDao reservaDao;

    public FuncionarioServiceImpl() {
        this(DaoFactory.obtenerFuncionarioDao(), DaoFactory.obtenerReservaDao());
    }

    public FuncionarioServiceImpl(FuncionarioDao funcionarioDao, ReservaDao reservaDao) {
        this.funcionarioDao = funcionarioDao;
        this.reservaDao = reservaDao;
    }

    public Optional<Funcionario> buscarPorId(String id) {
        return funcionarioDao.buscarPorId(id);
    }

    public List<Funcionario> buscarPorNombre(String nombre) {
        return funcionarioDao.buscarPorNombre(nombre);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioDao.listarTodos();
    }

    public void crear(Funcionario funcionario) {
        if (funcionario.getId() == null || funcionario.getId().isBlank()) {
            throw new ReglaDeNegocioException("El funcionario debe tener un ID.");
        }
        if (funcionario.getNombre() == null || funcionario.getNombre().isBlank()) {
            throw new ReglaDeNegocioException("El funcionario debe tener un nombre.");
        }
        if (funcionarioDao.buscarPorId(funcionario.getId()).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe un funcionario con ese ID.");
        }
        funcionario.setClave(funcionario.getId());
        funcionarioDao.insertar(funcionario);
    }

    public void modificar(Funcionario funcionario) {
        if (funcionario.getNombre() == null || funcionario.getNombre().isBlank()) {
            throw new ReglaDeNegocioException("El funcionario debe tener un nombre.");
        }
        Funcionario existente = funcionarioDao.buscarPorId(funcionario.getId())
                .orElseThrow(() -> new ReglaDeNegocioException("No existe un funcionario con ese ID."));
        funcionario.setClave(existente.getClave());
        funcionarioDao.actualizar(funcionario);
    }

    public void eliminar(String id) {
        if (funcionarioDao.buscarPorId(id).isEmpty()) {
            throw new ReglaDeNegocioException("No existe un funcionario con ese ID.");
        }
        boolean tieneReservaActiva = reservaDao.listarPorFuncionario(id).stream()
                .anyMatch(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA);
        if (tieneReservaActiva) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar el funcionario porque tiene reservas activas."
            );
        }
        funcionarioDao.eliminar(id);
    }
}
