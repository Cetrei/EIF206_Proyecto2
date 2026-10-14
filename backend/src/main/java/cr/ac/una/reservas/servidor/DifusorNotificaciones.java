package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.reserva.Reserva;
import cr.ac.una.reservas.reserva.ReservaObserver;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DifusorNotificaciones implements ReservaObserver {
    private static final Logger REGISTRO = Logger.getLogger(DifusorNotificaciones.class.getName());

    private final ServidorBackend servidor;
    private final FuncionarioService funcionarios;
    private final RecursoService recursos;

    public DifusorNotificaciones(
            ServidorBackend servidor, FuncionarioService funcionarios, RecursoService recursos
    ) {
        this.servidor = servidor;
        this.funcionarios = funcionarios;
        this.recursos = recursos;
    }

    @Override
    public void onReservaCreada(Reserva reserva) {
        try {
            servidor.difundirExcepto(construirAlerta(reserva), reserva.getIdFuncionario());
        } catch (RuntimeException excepcion) {
            REGISTRO.log(Level.WARNING, "No se pudo difundir la alerta de la reserva " + reserva.getId(), excepcion);
        }
    }

    @Override
    public void onReservaCancelada(Reserva reserva) {
    }

    @Override
    public void onReservaModificada(Reserva reserva) {
    }

    private AlertaReserva construirAlerta(Reserva reserva) {
        String encargado = funcionarios.buscarPorId(reserva.getIdFuncionario())
                .map(funcionario -> funcionario.getNombre())
                .orElse(reserva.getIdFuncionario());
        List<String> descripciones = recursos.listarTodos().stream()
                .filter(recurso -> reserva.getIdsRecursosAsignados().contains(recurso.getId()))
                .map(Recurso::getDescripcion)
                .toList();
        return new AlertaReserva(
                reserva.getId(),
                encargado,
                reserva.getActividad(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                descripciones
        );
    }
}
