package cr.ac.una.reservas.pruebas;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import javax.swing.SwingUtilities;
import java.awt.Dialog;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicBoolean;

public class CierreAutomaticoDeDialogos implements BeforeEachCallback, AfterEachCallback {

    private static final Namespace ESPACIO = Namespace.create(CierreAutomaticoDeDialogos.class);
    private static final String CLAVE = "activo";
    private static final long PERIODO_MS = 50;

    @Override
    public void beforeEach(ExtensionContext contexto) {
        AtomicBoolean activo = new AtomicBoolean(true);
        Thread vigilante = new Thread(() -> vigilar(activo), "cierre-automatico-de-dialogos");
        vigilante.setDaemon(true);
        vigilante.start();
        contexto.getStore(ESPACIO).put(CLAVE, activo);
    }

    @Override
    public void afterEach(ExtensionContext contexto) {
        AtomicBoolean activo = contexto.getStore(ESPACIO).remove(CLAVE, AtomicBoolean.class);
        if (activo != null) {
            activo.set(false);
        }
    }

    private static void vigilar(AtomicBoolean activo) {
        while (activo.get()) {
            for (Window ventana : Window.getWindows()) {
                if (ventana instanceof Dialog dialogo && dialogo.isModal() && dialogo.isVisible()) {
                    SwingUtilities.invokeLater(dialogo::dispose);
                }
            }
            try {
                Thread.sleep(PERIODO_MS);
            } catch (InterruptedException interrupcion) {
                return;
            }
        }
    }
}
