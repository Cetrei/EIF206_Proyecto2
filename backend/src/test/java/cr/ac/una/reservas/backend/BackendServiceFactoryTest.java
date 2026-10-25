package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.persistence.falso.ModoMemoria;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaObserver;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendServiceFactoryTest {
    static {
        ModoMemoria.activar();
    }


    @Test
    void devuelveSiempreLaMismaInstanciaDeCategoriaService() {
        assertSame(BackendServiceFactory.obtenerCategoriaService(), BackendServiceFactory.obtenerCategoriaService());
    }

    @Test
    void devuelveSiempreLaMismaInstanciaDeReservaService() {
        assertSame(BackendServiceFactory.obtenerReservaService(), BackendServiceFactory.obtenerReservaService());
    }

    @Test
    void unObservadorRegistradoEnUnaLlamadaSeEnteraDeCambiosNotificadosPorOtra() {
        int[] notificaciones = {0};
        CategoriaObserver observador = () -> notificaciones[0]++;
        BackendServiceFactory.obtenerCategoriaService().agregarObservador(observador);

        List<Categoria> antes = BackendServiceFactory.obtenerCategoriaService().listarTodas();
        Categoria creada = null;
        try {
            creada = BackendServiceFactory.obtenerCategoriaService().crear("Sala de prueba " + System.nanoTime());
            assertEquals(1, notificaciones[0]);
        } finally {
            if (creada != null) {
                BackendServiceFactory.obtenerCategoriaService().eliminar(creada.getId());
            }
            BackendServiceFactory.obtenerCategoriaService().quitarObservador(observador);
        }

        List<Categoria> despues = BackendServiceFactory.obtenerCategoriaService().listarTodas();
        assertEquals(antes.size(), despues.size());
        assertTrue(despues.stream().noneMatch(categoria -> categoria.getDescripcion().startsWith("Sala de prueba ")));
    }
}
