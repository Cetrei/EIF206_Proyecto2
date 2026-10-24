package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.ui.componentes.BarraSuperior;
import cr.ac.una.reservas.ui.componentes.BotonPrimario;
import cr.ac.una.reservas.ui.componentes.CampoTexto;
import cr.ac.una.reservas.ui.componentes.Tarjeta;
import cr.ac.una.reservas.ui.iconos.Icono;
import cr.ac.una.reservas.ui.tema.GestorTema;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public class DialogoCambioClave {
    private final Frame ventanaPropietaria;
    private final CampoTexto campoId = new CampoTexto(false);
    private final CampoTexto campoClaveActual = new CampoTexto(true);
    private final CampoTexto campoClaveNueva = new CampoTexto(true);
    private final BotonPrimario botonConfirmar = new BotonPrimario();
    private final JDialog dialogo;

    public DialogoCambioClave(Frame ventanaPropietaria) {
        this.ventanaPropietaria = ventanaPropietaria;
        configurarCampo(campoId, "ID", 30, Icono.USUARIO);
        configurarCampo(campoClaveActual, "CONTRASEÑA ACTUAL", 100, Icono.CANDADO);
        configurarCampo(campoClaveNueva, "CONTRASEÑA NUEVA", 100, Icono.CANDADO);
        botonConfirmar.setTexto("Confirmar");
        botonConfirmar.setIcono(Icono.CHECK);
        dialogo = armarDialogo(armarTarjeta());
    }

    public void alConfirmar(Runnable accion) {
        botonConfirmar.alHacerClick(accion);
    }

    public String obtenerId() {
        return campoId.obtenerTexto();
    }

    public String obtenerClaveActual() {
        return campoClaveActual.obtenerTexto();
    }

    public String obtenerClaveNueva() {
        return campoClaveNueva.obtenerTexto();
    }

    public void cerrar() {
        dialogo.dispose();
    }

    public void mostrar() {
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPropietaria);
        dialogo.setVisible(true);
    }

    private static void configurarCampo(CampoTexto campo, String etiqueta, int longitudMaxima, Icono icono) {
        campo.setEtiqueta(etiqueta);
        campo.setLongitudMaxima(longitudMaxima);
        campo.setObligatorio(true);
        campo.setIcono(icono);
    }

    private Tarjeta armarTarjeta() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTitulo("Cambiar contraseña");
        JPanel contenido = tarjeta.obtenerPanelContenido();
        contenido.setLayout(new GridLayout(4, 1, 0, 10));
        contenido.add(campoId.obtenerPanel());
        contenido.add(campoClaveActual.obtenerPanel());
        contenido.add(campoClaveNueva.obtenerPanel());
        contenido.add(botonConfirmar.obtenerPanel());
        return tarjeta;
    }

    private JDialog armarDialogo(Tarjeta tarjeta) {
        JDialog nuevo = new JDialog(ventanaPropietaria, "Cambiar contraseña", true);
        nuevo.setUndecorated(true);
        nuevo.setResizable(false);

        BarraSuperior barraSuperior = new BarraSuperior();
        barraSuperior.alCerrar(nuevo::dispose);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(true);
        envoltorio.setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());
        envoltorio.setBorder(new EmptyBorder(0, 16, 16, 16));
        envoltorio.add(barraSuperior.obtenerPanel(), BorderLayout.NORTH);
        envoltorio.add(tarjeta.obtenerPanel(), BorderLayout.CENTER);
        nuevo.setContentPane(envoltorio);
        return nuevo;
    }
}
