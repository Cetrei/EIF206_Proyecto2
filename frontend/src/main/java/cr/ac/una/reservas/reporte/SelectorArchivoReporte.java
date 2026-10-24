package cr.ac.una.reservas.reporte;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Frame;
import java.io.File;
import java.util.Locale;

public final class SelectorArchivoReporte {
    private SelectorArchivoReporte() {
    }

    public static File elegirArchivoPdf(Frame ventanaPropietaria, String nombreSugerido) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte PDF");
        selector.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        selector.setSelectedFile(new File(nombreSugerido + ".pdf"));

        int resultado = selector.showSaveDialog(ventanaPropietaria);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivoElegido = selector.getSelectedFile();
        if (!archivoElegido.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            archivoElegido = new File(archivoElegido.getParentFile(), archivoElegido.getName() + ".pdf");
        }
        return archivoElegido;
    }
}
