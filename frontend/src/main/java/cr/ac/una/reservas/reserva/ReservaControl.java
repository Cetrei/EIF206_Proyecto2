package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.categoria.CategoriaObserver;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.reporte.GeneracionReporteControl;
import cr.ac.una.reservas.reporte.ReporteFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservaControl implements CategoriaObserver {

    private final ReservaModel modelo;
    private final TabReservas vista;
    private final Frame ventanaPropietaria;
    private final ReservaService reservaService;
    private final CategoriaService categoriaService;
    private final ExtractorReservaService extractorReservaService;
    private final SolicitudReservaControl solicitudControl;
    private final ExtraccionIaControl extraccionIaControl;

    public ReservaControl(ReservaModel modelo, TabReservas vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerReservaService(),
                ServiceFactory.obtenerCategoriaService(),
                ServiceFactory.obtenerExtractorReservaService()
        );
    }

    public ReservaControl(
            ReservaModel modelo,
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService
    ) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                reservaService,
                categoriaService,
                ServiceFactory.obtenerExtractorReservaService()
        );
    }

    public ReservaControl(
            ReservaModel modelo,
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService,
            ExtractorReservaService extractorReservaService
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.reservaService = reservaService;
        this.categoriaService = categoriaService;
        this.extractorReservaService = extractorReservaService;
        this.solicitudControl = new SolicitudReservaControl(
                modelo, vista, ventanaPropietaria, reservaService, this::refrescarListado
        );
        this.extraccionIaControl = new ExtraccionIaControl(
                vista, ventanaPropietaria, categoriaService, extractorReservaService
        );

        this.vista.alSolicitarReserva(solicitudControl::solicitar);
        this.vista.alLimpiar(this::limpiar);
        this.vista.alCancelarSeleccionada(this::confirmarCancelacion);
        this.vista.alSeleccionarFila(this::seleccionarFila);
        this.vista.alGenerarReporte(this::generarReporte);
        this.vista.alExtraerConIA(extraccionIaControl::extraer);

        this.vista.mostrarEstadoIa(this.extractorReservaService.estaConfiguradoParaGemini());

        this.categoriaService.agregarObservador(this);

        refrescarCategorias();
        refrescarListado();
    }

    @Override
    public void onCategoriasCambiaron() {
        refrescarCategorias();
    }

    private void limpiar() {
        modelo.setReservaSeleccionada(null);
        vista.limpiarFormulario();
    }

    private void seleccionarFila(int indiceFila) {
        Reserva reserva = vista.reservaEnFila(indiceFila);
        if (reserva == null) return;
        modelo.setReservaSeleccionada(reserva);
    }

    private void confirmarCancelacion() {
        Reserva reservaSeleccionada = modelo.getReservaSeleccionada();
        if (reservaSeleccionada == null) {
            mostrarError("No se pudo cancelar", "Seleccione una reserva de la lista para cancelarla.");
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Cancelar reserva");
        popup.setMensaje(
                "¿Seguro desea cancelar la reserva \"" + reservaSeleccionada.getActividad() + "\"? "
                        + "Se liberarán todos los recursos asignados y esta acción no se puede deshacer."
        );

        List<Popup.AccionPopup> acciones = new ArrayList<>();
        acciones.add(new Popup.AccionPopup("Volver", false, null));
        acciones.add(new Popup.AccionPopup("Cancelar Reserva", true, this::cancelar));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void cancelar() {
        try {
            reservaService.cancelarReserva(modelo.getReservaSeleccionada().getId());
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Cancelar reserva",
                    "La reserva se canceló correctamente."
            );
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError("No se pudo cancelar", excepcion.getMessage());
        }
    }

    private void generarReporte() {
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.RESERVAS,
                "reservas",
                modelo.getReservas(),
                metadatosFiltroActual()
        );
    }

    private Map<String, String> metadatosFiltroActual() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();

        if (SesionControl.obtenerInstancia().esAdministrador()) {
            return Map.of("subtitulo", "Todas las reservas del sistema");
        }

        String nombre = usuarioActual instanceof Funcionario
                ? ((Funcionario) usuarioActual).getNombre()
                : usuarioActual == null ? "" : usuarioActual.getId();

        return Map.of("subtitulo", "Funcionario: " + nombre);
    }

    private void refrescarCategorias() {
        modelo.setCategoriasDisponibles(categoriaService.listarTodas());
    }

    private void refrescarListado() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();

        if (usuarioActual == null) {
            modelo.setReservas(List.of());
            return;
        }

        List<Reserva> reservas = SesionControl.obtenerInstancia().esAdministrador()
                ? reservaService.listarTodasOrdenadas()
                : reservaService.listarReservasDeFuncionario(usuarioActual.getId());

        modelo.setReservas(reservas);
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
