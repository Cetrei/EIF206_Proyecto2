package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.actividad.ActividadControl;
import cr.ac.una.reservas.actividad.ActividadModel;
import cr.ac.una.reservas.actividad.TabActividades;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.calendarizacion.CalendarizacionControl;
import cr.ac.una.reservas.calendarizacion.CalendarizacionModel;
import cr.ac.una.reservas.calendarizacion.TabCalendarizacion;
import cr.ac.una.reservas.categoria.CategoriaControl;
import cr.ac.una.reservas.categoria.CategoriaModel;
import cr.ac.una.reservas.categoria.TabCategorias;
import cr.ac.una.reservas.estadistica.EstadisticaControl;
import cr.ac.una.reservas.estadistica.EstadisticaModel;
import cr.ac.una.reservas.estadistica.TabEstadisticas;
import cr.ac.una.reservas.funcionario.FuncionarioControl;
import cr.ac.una.reservas.funcionario.FuncionarioModel;
import cr.ac.una.reservas.funcionario.TabFuncionarios;
import cr.ac.una.reservas.ia.ConfiguracionIaControl;
import cr.ac.una.reservas.ia.TabConfiguracionIa;
import cr.ac.una.reservas.recurso.RecursoControl;
import cr.ac.una.reservas.recurso.RecursoModel;
import cr.ac.una.reservas.recurso.TabRecursos;
import cr.ac.una.reservas.reserva.ReservaControl;
import cr.ac.una.reservas.reserva.ReservaModel;
import cr.ac.una.reservas.reserva.TabReservas;
import cr.ac.una.reservas.ui.componentes.ScrollBarTematizado;
import cr.ac.una.reservas.ui.tema.GestorTema;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Frame;

final class PestanasPrincipalControl {
    private final VentanaPrincipal vista;
    private final Frame ventanaPropietaria;

    PestanasPrincipalControl(VentanaPrincipal vista, Frame ventanaPropietaria) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
    }

    void inicializar() {
        if (SesionControl.obtenerInstancia().esAdministrador()) {
            inicializarPestanasDeAdministrador();
        }
        inicializarPestanasComunes();
    }

    private void inicializarPestanasDeAdministrador() {
        FuncionarioModel funcionarioModelo = new FuncionarioModel();
        TabFuncionarios tabFuncionarios = new TabFuncionarios(funcionarioModelo);
        new FuncionarioControl(funcionarioModelo, tabFuncionarios, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelFuncionarios(), tabFuncionarios.obtenerPanel());

        CategoriaModel categoriaModelo = new CategoriaModel();
        TabCategorias tabCategorias = new TabCategorias(categoriaModelo);
        new CategoriaControl(categoriaModelo, tabCategorias, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCategorias(), tabCategorias.obtenerPanel());

        RecursoModel recursoModelo = new RecursoModel();
        TabRecursos tabRecursos = new TabRecursos(recursoModelo);
        new RecursoControl(recursoModelo, tabRecursos, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelRecursos(), tabRecursos.obtenerPanel());

        TabConfiguracionIa tabConfiguracionIa = new TabConfiguracionIa();
        new ConfiguracionIaControl(tabConfiguracionIa, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelConfiguracionIa(), tabConfiguracionIa.obtenerPanel());
    }

    private void inicializarPestanasComunes() {
        CalendarizacionModel calendarizacionModelo = new CalendarizacionModel();
        TabCalendarizacion tabCalendarizacion = new TabCalendarizacion(calendarizacionModelo);
        new CalendarizacionControl(calendarizacionModelo, tabCalendarizacion, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCalendarizacion(), tabCalendarizacion.obtenerPanel());

        ActividadModel actividadModelo = new ActividadModel();
        TabActividades tabActividades = new TabActividades(actividadModelo);
        new ActividadControl(actividadModelo, tabActividades, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelActividades(), tabActividades.obtenerPanel());

        EstadisticaModel estadisticaModelo = new EstadisticaModel();
        TabEstadisticas tabEstadisticas = new TabEstadisticas(estadisticaModelo);
        new EstadisticaControl(estadisticaModelo, tabEstadisticas, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelEstadisticas(), tabEstadisticas.obtenerPanel());

        ReservaModel reservaModelo = new ReservaModel();
        TabReservas tabReservas = new TabReservas(reservaModelo);
        new ReservaControl(reservaModelo, tabReservas, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelReservas(), tabReservas.obtenerPanel());
    }

    private void colocarEnPestana(JPanel panelPestana, JPanel contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        ScrollBarTematizado.aplicar(scroll.getVerticalScrollBar(), GestorTema.obtenerInstancia().temaActivo());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelPestana.setLayout(new BorderLayout());
        panelPestana.setBorder(BorderFactory.createEmptyBorder());
        panelPestana.add(scroll, BorderLayout.CENTER);
    }
}
