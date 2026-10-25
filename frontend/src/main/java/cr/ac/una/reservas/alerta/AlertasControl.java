package cr.ac.una.reservas.alerta;

import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.protocol.Notificacion;
import cr.ac.una.reservas.protocol.SesionTerminada;
import cr.ac.una.reservas.proxy.EnrutadorNotificaciones;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlertasControl {
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final AlertasModel modelo;
    private final PanelAlertas vista;
    private final Frame ventanaPropietaria;

    public AlertasControl(AlertasModel modelo, PanelAlertas vista, Frame ventanaPropietaria) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.modelo.addPropertyChangeListener(evento -> vista.setAlertas(modelo.getAlertas()));
        this.vista.alVer(this::mostrarDetalle);
        this.vista.alDescartar(this::confirmarDescarte);
    }

    public static JPanel envolver(Frame ventana, JPanel contenido) {
        PanelAlertas panelAlertas = new PanelAlertas();
        AlertasControl control = new AlertasControl(new AlertasModel(), panelAlertas, ventana);
        EnrutadorNotificaciones.obtenerInstancia().establecerDestino(control::recibir);
        return panelAlertas.envolver(contenido);
    }

    public void recibir(Notificacion notificacion) {
        if (notificacion instanceof AlertaReserva alerta) {
            SwingUtilities.invokeLater(() -> modelo.agregar(alerta));
        } else if (notificacion instanceof SesionTerminada terminada) {
            ReaccionExcepcion.sesionTerminada(ventanaPropietaria, terminada.getMensaje());
        }
    }

    public void limpiar() {
        SwingUtilities.invokeLater(modelo::limpiar);
    }

    private void mostrarDetalle(AlertaReserva alerta) {
        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.INFORMACION);
        popup.setTitulo(alerta.getActividad());
        popup.setMensaje(describir(alerta));
        popup.setAcciones(List.of(new Popup.AccionPopup("Aceptar", true, null)));
        popup.mostrar();
    }

    private void confirmarDescarte(AlertaReserva alerta) {
        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.CONFIRMACION);
        popup.setTitulo("¿Descartar?");
        popup.setMensaje(alerta.getActividad());
        popup.setAcciones(List.of(
                new Popup.AccionPopup("Sí", true, () -> modelo.descartar(alerta)),
                new Popup.AccionPopup("No", false, null)
        ));
        popup.mostrar();
    }

    private static String describir(AlertaReserva alerta) {
        StringBuilder texto = new StringBuilder();
        texto.append("Encargado: ").append(alerta.getEncargado()).append("<br>");
        texto.append("Fecha: ").append(alerta.getFecha()).append("<br>");
        texto.append("Desde: ").append(alerta.getHoraInicio().format(FORMATO_HORA)).append("<br>");
        texto.append("Hasta: ").append(alerta.getHoraFin().format(FORMATO_HORA)).append("<br><br>");
        texto.append("Recursos:<table border=1 cellspacing=0 cellpadding=4 width=200>");
        for (String recurso : alerta.getDescripcionesRecursos()) {
            texto.append("<tr><td>").append(recurso).append("</td></tr>");
        }
        texto.append("</table>");
        return texto.toString();
    }
}
