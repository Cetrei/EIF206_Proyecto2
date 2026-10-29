package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.persistence.falso.AdministradorDaoFalso;
import cr.ac.una.reservas.persistence.falso.FuncionarioDaoFalso;
import cr.ac.una.reservas.ui.componentes.CampoTexto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Container;
import java.awt.Frame;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControlTest {

    private static final String ID_ADMIN = "admin";
    private static final String CLAVE_ADMIN = "123";

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void recortaEspaciosDelIdAlIngresar() throws Exception {
        AdministradorDaoFalso administradorDao = new AdministradorDaoFalso();
        administradorDao.insertar(new Administrador(ID_ADMIN, CLAVE_ADMIN));
        AutenticacionService autenticacionService = new AutenticacionServiceImpl(administradorDao, new FuncionarioDaoFalso());

        LoginModel modelo = new LoginModel();
        LoginPanel vista = new LoginPanel(modelo);
        Frame ventanaPropietaria = new Frame();
        new LoginControl(modelo, vista, ventanaPropietaria, autenticacionService);

        campoTexto(vista.obtenerPanel()).setText(" " + ID_ADMIN + " ");
        campoClave(vista.obtenerPanel()).setText(CLAVE_ADMIN);
        campoClave(vista.obtenerPanel()).postActionEvent();

        esperarSesion();
        assertTrue(SesionControl.obtenerInstancia().haySesionActiva());
        assertNull(modelo.getMensajeError());
    }

    @Test
    void recortaEspaciosDelIdAlCambiarClave() throws Exception {
        AdministradorDaoFalso administradorDao = new AdministradorDaoFalso();
        administradorDao.insertar(new Administrador(ID_ADMIN, CLAVE_ADMIN));
        AutenticacionService autenticacionService = new AutenticacionServiceImpl(administradorDao, new FuncionarioDaoFalso());

        LoginModel modelo = new LoginModel();
        LoginPanel vista = new LoginPanel(modelo);
        Frame ventanaPropietaria = new Frame();
        LoginControl control = new LoginControl(modelo, vista, ventanaPropietaria, autenticacionService);

        DialogoCambioClave dialogo = new DialogoCambioClave(ventanaPropietaria);
        asignarCampo(dialogo, "campoId", " " + ID_ADMIN + " ");
        asignarCampo(dialogo, "campoClaveActual", CLAVE_ADMIN);
        asignarCampo(dialogo, "campoClaveNueva", "nueva-clave");

        invocarConfirmarCambioClave(control, dialogo);

        assertEquals("nueva-clave", administradorDao.buscarPorId(ID_ADMIN).get().getClave());
    }

    private static void esperarSesion() throws InterruptedException {
        long limite = System.currentTimeMillis() + 5000;
        while (!SesionControl.obtenerInstancia().haySesionActiva() && System.currentTimeMillis() < limite) {
            Thread.sleep(25);
        }
    }

    private static void asignarCampo(DialogoCambioClave dialogo, String nombreCampo, String valor) throws Exception {
        Field campo = DialogoCambioClave.class.getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        ((CampoTexto) campo.get(dialogo)).mostrarValor(valor);
    }

    private static void invocarConfirmarCambioClave(LoginControl control, DialogoCambioClave dialogo) throws Exception {
        Method metodo = LoginControl.class.getDeclaredMethod("confirmarCambioClave", DialogoCambioClave.class);
        metodo.setAccessible(true);
        metodo.invoke(control, dialogo);
    }

    private static JTextField campoTexto(Container contenedor) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof JPasswordField) continue;
            if (componente instanceof JTextField) return (JTextField) componente;
            if (componente instanceof Container) {
                JTextField encontrado = campoTexto((Container) componente);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static JPasswordField campoClave(Container contenedor) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof JPasswordField) return (JPasswordField) componente;
            if (componente instanceof Container) {
                JPasswordField encontrado = campoClave((Container) componente);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
