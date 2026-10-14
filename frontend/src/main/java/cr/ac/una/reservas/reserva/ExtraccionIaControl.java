package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.ia.DatosReservaExtraidos;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.SwingWorker;
import java.awt.Frame;
import java.util.List;
import java.util.concurrent.ExecutionException;

final class ExtraccionIaControl {
    private final TabReservas vista;
    private final Frame ventanaPropietaria;
    private final CategoriaService categoriaService;
    private final ExtractorReservaService extractorReservaService;

    ExtraccionIaControl(
            TabReservas vista,
            Frame ventanaPropietaria,
            CategoriaService categoriaService,
            ExtractorReservaService extractorReservaService
    ) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.categoriaService = categoriaService;
        this.extractorReservaService = extractorReservaService;
    }

    void extraer() {
        String frase = vista.obtenerFraseIa();

        if (frase == null || frase.isBlank()) {
            mostrarError("Extraer datos con IA", "Escriba primero una frase describiendo la reserva.");
            return;
        }

        Popup popupCargando = Popup.mostrarCargando(
                ventanaPropietaria,
                "Extraer datos con IA",
                "Analizando la frase e identificando los datos de la reserva..."
        );

        SwingWorker<DatosReservaExtraidos, Void> worker = new SwingWorker<DatosReservaExtraidos, Void>() {
            @Override
            protected DatosReservaExtraidos doInBackground() {
                List<Categoria> categorias = categoriaService.listarTodas();
                return extractorReservaService.extraer(frase, categorias);
            }

            @Override
            protected void done() {
                popupCargando.cerrar();
                try {
                    procesarResultadoExtraccion(get());
                } catch (ExecutionException excepcion) {
                    procesarFalloExtraccion();
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                    procesarFalloExtraccion();
                }
            }
        };
        worker.execute();
    }

    private void procesarResultadoExtraccion(DatosReservaExtraidos datos) {
        boolean datoEncontrado = false;

        if (datos.getActividad() != null && !datos.getActividad().isBlank()) {
            vista.mostrarActividad(datos.getActividad());
            datoEncontrado = true;
        }

        if (datos.getFecha() != null) {
            vista.mostrarFecha(datos.getFecha());
            datoEncontrado = true;
        }

        if (datos.getHoraInicio() != null) {
            vista.mostrarHoraInicio(datos.getHoraInicio());
            datoEncontrado = true;
        }

        if (datos.getHoraFin() != null) {
            vista.mostrarHoraFin(datos.getHoraFin());
            datoEncontrado = true;
        }

        if (datos.getIdsCategoriasIdentificadas() != null && !datos.getIdsCategoriasIdentificadas().isEmpty()) {
            vista.mostrarCategoriasSeleccionadas(datos.getIdsCategoriasIdentificadas());
            datoEncontrado = true;
        }

        if (!datoEncontrado) {
            vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.INFORMACION,
                    "Extraer datos con IA",
                    mensajeSinDatos()
            );
            return;
        }

        vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.CONFIRMACION, "Datos extraídos", mensajeConDatos());
    }

    private String mensajeSinDatos() {
        String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();

        if (motivoFallo != null) {
            return "Gemini no pudo procesar la solicitud (" + motivoFallo + "). "
                    + "Se intentó con el modo básico de respaldo, pero tampoco identificó datos suficientes. "
                    + "Puede completar el formulario manualmente.";
        }
        if (extractorReservaService.fueUsadoModoBasico()) {
            return "El modo básico de respaldo no identificó datos suficientes en la frase. "
                    + "Puede completar el formulario manualmente.";
        }
        return "Gemini respondió correctamente pero no identificó datos suficientes en la frase. "
                + "Puede completar el formulario manualmente.";
    }

    private String mensajeConDatos() {
        if (extractorReservaService.fueUsadoModoBasico()) {
            String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();
            String detalleFallo = motivoFallo != null ? " Motivo: " + motivoFallo + "." : "";

            return "Gemini no estuvo disponible." + detalleFallo + " "
                    + "Los datos identificados fueron cargados usando el modo básico de respaldo. "
                    + "Revise la información antes de solicitar la reserva.";
        }
        return "Los datos identificados por Gemini fueron cargados en el formulario. "
                + "Revise la información antes de solicitar la reserva.";
    }

    private void procesarFalloExtraccion() {
        vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());

        String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();
        String detalle = motivoFallo != null ? " (" + motivoFallo + ")" : "";

        mostrarError(
                "Extraer datos con IA",
                "No se pudieron extraer los datos de la reserva" + detalle + ". "
                        + "Puede completar el formulario manualmente."
        );
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
