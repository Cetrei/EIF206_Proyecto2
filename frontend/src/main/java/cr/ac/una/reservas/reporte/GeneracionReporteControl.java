package cr.ac.una.reservas.reporte;

import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.ui.componentes.Popup;

import java.awt.Frame;
import java.io.File;
import java.util.List;
import java.util.Map;

public final class GeneracionReporteControl {
    private GeneracionReporteControl() {
    }

    @SuppressWarnings("unchecked")
    public static <T> void generarYGuardar(
            Frame ventanaPropietaria,
            ReporteFactory.TipoReporte tipoReporte,
            String nombreSugerido,
            List<T> datos,
            Map<String, String> metadatos
    ) {
        File archivoDestino = SelectorArchivoReporte.elegirArchivoPdf(ventanaPropietaria, nombreSugerido);
        if (archivoDestino == null) {
            return;
        }

        try {
            GeneradorReporte<T> generador = (GeneradorReporte<T>) ReporteFactory.obtenerGenerador(tipoReporte);
            generador.generar(archivoDestino.getAbsolutePath(), datos, metadatos);
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Generar reporte",
                    "El reporte se generó correctamente en:\n" + archivoDestino.getAbsolutePath()
            );
        } catch (ReservaAppException excepcion) {
            ReaccionExcepcion.mostrar(ventanaPropietaria, "No se pudo generar el reporte", excepcion);
        } catch (IllegalStateException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.INFORMACION,
                    "Generar reporte",
                    "La generación de reportes en PDF todavía no está disponible para esta pantalla."
            );
        }
    }
}
