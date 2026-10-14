package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.ui.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class AlertasModel extends AbstractModel {
    public static final String PROP_ALERTAS = "alertas";

    private final List<AlertaReserva> alertas = new ArrayList<>();

    public List<AlertaReserva> getAlertas() {
        return List.copyOf(alertas);
    }

    public void agregar(AlertaReserva alerta) {
        List<AlertaReserva> anteriores = getAlertas();
        alertas.add(0, alerta);
        notificarCambio(PROP_ALERTAS, anteriores, getAlertas());
    }

    public void descartar(AlertaReserva alerta) {
        List<AlertaReserva> anteriores = getAlertas();
        if (alertas.remove(alerta)) {
            notificarCambio(PROP_ALERTAS, anteriores, getAlertas());
        }
    }

    public void limpiar() {
        List<AlertaReserva> anteriores = getAlertas();
        if (!alertas.isEmpty()) {
            alertas.clear();
            notificarCambio(PROP_ALERTAS, anteriores, getAlertas());
        }
    }
}
