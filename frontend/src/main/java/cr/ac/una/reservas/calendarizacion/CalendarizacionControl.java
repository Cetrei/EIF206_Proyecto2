package cr.ac.una.reservas.calendarizacion;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaObserver;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.reporte.FilaCalendarizacion;
import cr.ac.una.reservas.reporte.GeneracionReporteControl;
import cr.ac.una.reservas.reporte.ReporteFactory;
import cr.ac.una.reservas.reserva.Reserva;
import cr.ac.una.reservas.reserva.ReservaObserver;
import cr.ac.una.reservas.reserva.ReservaService;
import cr.ac.una.reservas.ui.componentes.Popup;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarizacionControl implements ReservaObserver, CategoriaObserver {
    private final CalendarizacionModel modelo;
    private final TabCalendarizacion vista;
    private final Frame ventanaPropietaria;
    private final RecursoService recursoService;
    private final ReservaService reservaService;
    private final CategoriaService categoriaService;

    public CalendarizacionControl(CalendarizacionModel modelo, TabCalendarizacion vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerRecursoService(),
                ServiceFactory.obtenerReservaService(),
                ServiceFactory.obtenerCategoriaService()
        );
    }

    public CalendarizacionControl(
            CalendarizacionModel modelo,
            TabCalendarizacion vista,
            Frame ventanaPropietaria,
            RecursoService recursoService,
            ReservaService reservaService,
            CategoriaService categoriaService
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.recursoService = recursoService;
        this.reservaService = reservaService;
        this.categoriaService = categoriaService;

        this.vista.alCargarMatriz(this::cargarMatriz);
        this.vista.alGenerarReporte(this::generarReporte);

        this.reservaService.agregarObservador(this);
        this.categoriaService.agregarObservador(this);

        refrescarCategorias();
        cargarMatriz();
    }

    @Override
    public void onCategoriasCambiaron() {
        refrescarCategorias();
    }

    private void cargarMatriz() {
        try {
            LocalDate fecha = vista.obtenerFecha();
            if (fecha == null) {
                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.ERROR,
                        "No se pudo cargar",
                        "Debe seleccionar una fecha valida para consultar la disponibilidad."
                );
                return;
            }
            Categoria categoria = vista.obtenerCategoriaSeleccionada();

            CalendarizacionRecursoStrategy estrategia = new CalendarizacionRecursoStrategy(
                    recursoService, reservaService, categoria, fecha, this::alHacerClickCeldaOcupada
            );
            modelo.setMatriz(estrategia);
        } catch (ReservaAppException excepcion) {
            ReaccionExcepcion.mostrar(ventanaPropietaria, "No se pudo cargar", excepcion);
        } catch (RuntimeException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo cargar",
                    "No fue posible consultar la disponibilidad con los filtros seleccionados."
            );
        }
    }

    private void alHacerClickCeldaOcupada(Recurso recurso, Reserva reserva) {
        Popup.mostrarAviso(
                ventanaPropietaria,
                Popup.Tipo.INFORMACION,
                "Recurso ocupado",
                "\"" + recurso.getDescripcion() + "\" está reservado para \"" + reserva.getActividad() + "\" de "
                        + reserva.getHoraInicio() + " a " + reserva.getHoraFin() + "."
        );
    }

    private void generarReporte() {
        LocalDate fecha = vista.obtenerFecha();
        if (fecha == null) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo generar el reporte",
                    "Debe seleccionar una fecha valida antes de generar el reporte."
            );
            return;
        }

        try {
            Categoria categoria = vista.obtenerCategoriaSeleccionada();
            String subtitulo = "Fecha: " + fecha
                    + (categoria == null ? "" : " · Categoría: " + categoria.getDescripcion());

            List<FilaCalendarizacion> filas = armarFilasReporte(categoria, fecha);

            GeneracionReporteControl.generarYGuardar(
                    ventanaPropietaria,
                    ReporteFactory.TipoReporte.CALENDARIZACION,
                    "calendarizacion",
                    filas,
                    Map.of("subtitulo", subtitulo)
            );
        } catch (ReservaAppException excepcion) {
            ReaccionExcepcion.mostrar(
                    ventanaPropietaria, "No se pudo generar el reporte", excepcion
            );
        } catch (RuntimeException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo generar el reporte",
                    "No fue posible generar el reporte con los filtros seleccionados."
            );
        }
    }

    private List<FilaCalendarizacion> armarFilasReporte(Categoria categoria, LocalDate fecha) {
        List<FilaCalendarizacion> filas = new ArrayList<>();
        if (categoria == null || fecha == null) return filas;

        List<Recurso> recursos = recursoService.listarPorCategoria(categoria.getId());
        List<Reserva> reservasDelDia = reservaService.listarReservasActivasEnFecha(fecha);

        for (Recurso recurso : recursos) {
            List<Reserva> reservasDelRecurso = new ArrayList<>();
            for (Reserva reserva : reservasDelDia) {
                if (reserva.getIdsRecursosAsignados().contains(recurso.getId())) {
                    reservasDelRecurso.add(reserva);
                }
            }

            if (reservasDelRecurso.isEmpty()) {
                filas.add(new FilaCalendarizacion(recurso.getDescripcion(), "Todo el día", "Disponible"));
                continue;
            }
            for (Reserva reserva : reservasDelRecurso) {
                filas.add(new FilaCalendarizacion(
                        recurso.getDescripcion(),
                        reserva.getHoraInicio() + " - " + reserva.getHoraFin(),
                        reserva.getActividad()
                ));
            }
        }
        return filas;
    }

    private void refrescarCategorias() {
        modelo.setCategoriasDisponibles(categoriaService.listarTodas());
    }

    @Override
    public void onReservaCreada(Reserva reserva) {
        cargarMatriz();
    }

    @Override
    public void onReservaCancelada(Reserva reserva) {
        cargarMatriz();
    }
}
