package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ErrorInternoException;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.recurso.Recurso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReservaServiceImpl implements ReservaService {
    private static final Logger REGISTRO = Logger.getLogger(ReservaServiceImpl.class.getName());
    private static final Semaphore PERMISO_RESERVAS = new Semaphore(1, true);

    private final ReservaDao reservaDao;
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;
    private final List<ReservaObserver> observadores = new CopyOnWriteArrayList<>();

    public ReservaServiceImpl() {
        this(DaoFactory.obtenerReservaDao(), DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao());
    }

    public ReservaServiceImpl(ReservaDao reservaDao, RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.reservaDao = reservaDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Reserva> listarReservasDeFuncionario(String idFuncionario) {
        return reservaDao.listarPorFuncionario(idFuncionario);
    }

    public List<Reserva> listarTodasOrdenadas() {
        return reservaDao.listarTodos().stream()
                .sorted(Comparator
                        .comparing(Reserva::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Reserva::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Reserva> listarReservasActivasEnFecha(LocalDate fecha) {
        return reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .filter(reserva -> fecha.equals(reserva.getFecha()))
                .collect(Collectors.toList());
    }

    public ResultadoReserva intentarReservar(DatosNuevaReserva datos) {
        validarDatosBasicos(datos);
        ResultadoReserva resultado = enExclusion(() -> reservarEnExclusion(datos));
        if (resultado.isExitoso()) {
            notificarReservaCreada(resultado.getReserva());
        }
        return resultado;
    }

    public ResultadoReserva intentarModificar(DatosNuevaReserva datos) {
        if (datos.getId() == null) {
            throw new ReglaDeNegocioException("Debe indicar la reserva que se desea modificar.");
        }
        validarDatosBasicos(datos);
        ResultadoReserva resultado = enExclusion(() -> modificarEnExclusion(datos));
        if (resultado.isExitoso()) {
            notificarReservaModificada(resultado.getReserva());
        }
        return resultado;
    }

    public void cancelarReserva(Integer idReserva) {
        Reserva cancelada = enExclusion(() -> cancelarEnExclusion(idReserva));
        notificarReservaCancelada(cancelada);
    }

    private <T> T enExclusion(Supplier<T> accion) {
        try {
            PERMISO_RESERVAS.acquire();
        } catch (InterruptedException interrupcion) {
            Thread.currentThread().interrupt();
            throw new ErrorInternoException();
        }
        try {
            return accion.get();
        } finally {
            PERMISO_RESERVAS.release();
        }
    }

    private ResultadoReserva reservarEnExclusion(DatosNuevaReserva datos) {
        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAAsignar = new ArrayList<>();

        for (Integer idCategoria : datos.getIdsCategoriasRequeridas()) {
            Optional<Recurso> recursoDisponible = buscarPrimerRecursoDisponible(
                    idCategoria, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin(), null
            );
            if (recursoDisponible.isPresent()) {
                recursosAAsignar.add(recursoDisponible.get());
            } else {
                categoriaDao.buscarPorId(idCategoria).ifPresent(categoriasNoDisponibles::add);
            }
        }

        if (!categoriasNoDisponibles.isEmpty()) {
            return ResultadoReserva.fracaso(categoriasNoDisponibles);
        }

        datos.setId(null);
        Reserva reserva = new Reserva(datos);
        reserva.setIdsRecursosAsignados(
                recursosAAsignar.stream().map(Recurso::getId).collect(Collectors.toList())
        );
        Reserva guardada = reservaDao.insertar(reserva);
        return ResultadoReserva.exito(guardada);
    }

    private ResultadoReserva modificarEnExclusion(DatosNuevaReserva datos) {
        Reserva reservaExistente = reservaDao.buscarPorId(datos.getId())
                .orElseThrow(() -> new ReglaDeNegocioException("No existe una reserva con ese ID."));

        if (reservaExistente.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaDeNegocioException("No se puede modificar una reserva cancelada.");
        }

        if (datos.getIdFuncionario() != null
                && !datos.getIdFuncionario().equals(reservaExistente.getIdFuncionario())) {
            throw new ReglaDeNegocioException("Solo el funcionario dueño de la reserva puede modificarla.");
        }

        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAAsignar = new ArrayList<>();

        for (Integer idCategoria : datos.getIdsCategoriasRequeridas()) {
            Optional<Recurso> recursoDisponible = buscarPrimerRecursoDisponible(
                    idCategoria, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin(), reservaExistente.getId()
            );
            if (recursoDisponible.isPresent()) {
                recursosAAsignar.add(recursoDisponible.get());
            } else {
                categoriaDao.buscarPorId(idCategoria).ifPresent(categoriasNoDisponibles::add);
            }
        }

        if (!categoriasNoDisponibles.isEmpty()) {
            return ResultadoReserva.fracaso(categoriasNoDisponibles);
        }

        reservaExistente.setActividad(datos.getActividad());
        reservaExistente.setFecha(datos.getFecha());
        reservaExistente.setHoraInicio(datos.getHoraInicio());
        reservaExistente.setHoraFin(datos.getHoraFin());
        reservaExistente.setIdsCategoriasRequeridas(new ArrayList<>(datos.getIdsCategoriasRequeridas()));
        reservaExistente.setIdsRecursosAsignados(
                recursosAAsignar.stream().map(Recurso::getId).collect(Collectors.toList())
        );
        reservaDao.actualizar(reservaExistente);
        return ResultadoReserva.exito(reservaExistente);
    }

    private Reserva cancelarEnExclusion(Integer idReserva) {
        Reserva reserva = reservaDao.buscarPorId(idReserva)
                .orElseThrow(() -> new ReglaDeNegocioException("No existe una reserva con ese ID."));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaDeNegocioException("La reserva ya está cancelada.");
        }
        if (reserva.getFecha() != null && reserva.getFecha().isBefore(LocalDate.now())) {
            throw new ReglaDeNegocioException("No se puede cancelar una reserva que ya pasó.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaDao.actualizar(reserva);
        return reserva;
    }

    public void agregarObservador(ReservaObserver observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ReservaObserver observador) {
        observadores.remove(observador);
    }

    private void notificarReservaCreada(Reserva reserva) {
        notificar(observador -> observador.onReservaCreada(reserva));
    }

    private void notificarReservaCancelada(Reserva reserva) {
        notificar(observador -> observador.onReservaCancelada(reserva));
    }

    private void notificarReservaModificada(Reserva reserva) {
        notificar(observador -> observador.onReservaModificada(reserva));
    }

    private void notificar(Consumer<ReservaObserver> accion) {
        for (ReservaObserver observador : observadores) {
            try {
                accion.accept(observador);
            } catch (RuntimeException excepcion) {
                REGISTRO.log(Level.WARNING, "Un observador de reservas fallo al ser notificado", excepcion);
            }
        }
    }

    private void validarDatosBasicos(DatosNuevaReserva datos) {
        if (datos.getActividad() == null || datos.getActividad().isBlank()) {
            throw new ReglaDeNegocioException("Debe indicar la actividad de la reserva.");
        }
        if (datos.getFecha() == null) {
            throw new ReglaDeNegocioException("Debe indicar la fecha de la reserva.");
        }
        if (datos.getFecha().isBefore(LocalDate.now())) {
            throw new ReglaDeNegocioException("La fecha de la reserva no puede ser en el pasado.");
        }
        if (datos.getHoraInicio() == null || datos.getHoraFin() == null) {
            throw new ReglaDeNegocioException("Debe indicar la hora de inicio y de finalización.");
        }
        if (!datos.getHoraFin().isAfter(datos.getHoraInicio())) {
            throw new ReglaDeNegocioException("La hora de finalización debe ser posterior a la hora de inicio.");
        }
        if (datos.getIdsCategoriasRequeridas() == null || datos.getIdsCategoriasRequeridas().isEmpty()) {
            throw new ReglaDeNegocioException("Debe seleccionar al menos una categoría de recurso.");
        }
    }

    private Optional<Recurso> buscarPrimerRecursoDisponible(
            Integer idCategoria, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Integer idReservaExcluida
    ) {
        List<Recurso> recursosDeLaCategoria = recursoDao.listarPorCategoria(idCategoria);
        List<Reserva> reservasActivasEseDia = reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .filter(reserva -> idReservaExcluida == null || !idReservaExcluida.equals(reserva.getId()))
                .filter(reserva -> fecha.equals(reserva.getFecha()))
                .filter(reserva -> seSolapan(horaInicio, horaFin, reserva.getHoraInicio(), reserva.getHoraFin()))
                .collect(Collectors.toList());

        for (Recurso recurso : recursosDeLaCategoria) {
            boolean ocupado = reservasActivasEseDia.stream()
                    .anyMatch(reserva -> reserva.getIdsRecursosAsignados().contains(recurso.getId()));
            if (!ocupado) {
                return Optional.of(recurso);
            }
        }
        return Optional.empty();
    }

    private boolean seSolapan(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }
}
