package cr.ac.una.reservas.util;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ArchivoConfiguracion {
    private static final String NOMBRE = "config.properties";

    private ArchivoConfiguracion() {
    }

    public static Path ubicar(String modulo) {
        Path local = Path.of(NOMBRE);
        if (Files.exists(local)) {
            return local;
        }
        Path enModulo = Path.of(modulo, NOMBRE);
        return Files.exists(enModulo) ? enModulo : local;
    }
}
