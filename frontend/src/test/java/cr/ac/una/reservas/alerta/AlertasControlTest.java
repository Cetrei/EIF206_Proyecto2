package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.protocol.Notificacion;

import org.junit.jupiter.api.Test;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertasControlTest {

    private static final class OtraNotificacion implements Notificacion {
        private static final long serialVersionUID = 1L;
    }

    private static AlertaReserva alerta(int id, String actividad) {
        return new AlertaReserva(
                id, "Juan Mora", actividad, LocalDate.of(2026, 11, 1),
                LocalTime.of(8, 0), LocalTime.of(10, 0), List.of("Laptop #238719")
        );
    }

    private static void esperarCola() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    @Test
    void laAlertaMasRecienteQuedaArriba() throws Exception {
        AlertasModel modelo = new AlertasModel();
        AlertasControl control = new AlertasControl(modelo, new PanelAlertas(), null);

        control.recibir(alerta(1, "Reunion"));
        control.recibir(alerta(2, "Taller"));
        esperarCola();

        assertEquals(List.of(2, 1), modelo.getAlertas().stream().map(AlertaReserva::getIdReserva).toList());
    }

    @Test
    void ignoraNotificacionesQueNoSonAlertas() throws Exception {
        AlertasModel modelo = new AlertasModel();
        AlertasControl control = new AlertasControl(modelo, new PanelAlertas(), null);

        control.recibir(new OtraNotificacion());
        esperarCola();

        assertTrue(modelo.getAlertas().isEmpty());
    }

    @Test
    void descartarQuitaSoloLaAlertaIndicada() {
        AlertasModel modelo = new AlertasModel();
        AlertaReserva primera = alerta(1, "Reunion");
        AlertaReserva segunda = alerta(2, "Taller");
        modelo.agregar(primera);
        modelo.agregar(segunda);

        modelo.descartar(primera);

        assertEquals(List.of(segunda), modelo.getAlertas());
    }

    @Test
    void cerrarSesionLimpiaLaLista() throws Exception {
        AlertasModel modelo = new AlertasModel();
        AlertasControl control = new AlertasControl(modelo, new PanelAlertas(), null);
        control.recibir(alerta(1, "Reunion"));
        esperarCola();

        control.limpiar();
        esperarCola();

        assertTrue(modelo.getAlertas().isEmpty());
    }
}
