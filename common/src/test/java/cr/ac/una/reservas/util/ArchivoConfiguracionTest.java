package cr.ac.una.reservas.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArchivoConfiguracionTest {

    @Test
    void sinArchivoEnNingunSitioDevuelveElNombreLocal() {
        assertEquals(Path.of("config.properties"), ArchivoConfiguracion.ubicar("modulo-inexistente"));
    }
}
