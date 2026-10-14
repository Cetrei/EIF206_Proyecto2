package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.proxy.ConexionBackend;
import cr.ac.una.reservas.proxy.ProveedorRemoto;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.servidor.RutasBackend;
import cr.ac.una.reservas.servidor.ServidorBackend;
import cr.ac.una.reservas.ui.componentes.PanelCuenta;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.awt.Frame;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VentanaPrincipalControlIT {

    private static ServidorBackend servidor;
    private static ConexionBackend conexion;
    private static FuncionarioService funcionarioService;

    @BeforeAll
    static void iniciarServidorYConectar() throws IOException {
        servidor = new ServidorBackend(RutasBackend.crearDespachador());
        servidor.iniciar(0);
        conexion = new ConexionBackend("localhost", servidor.getPuertoLocal(), notificacion -> { });
        conexion.conectar();
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);
        proveedor.autenticacion().autenticar("admin", "123");
        ServiceFactory.instalar(proveedor);
        funcionarioService = ServiceFactory.obtenerFuncionarioService();
    }

    @AfterAll
    static void cerrarConexionYServidor() {
        conexion.close();
        servidor.close();
    }

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void recortaEspaciosDelTelefonoAlGuardarMiCuenta() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        funcionarioService.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        CuentaControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "  8888-8888  ");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("8888-8888", funcionarioService.buscarPorId(id).get().getTelefono());
    }

    @Test
    void rechazaTelefonoConCaracteresInvalidosSinModificarElFuncionario() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        funcionarioService.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        CuentaControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "no-es-un-telefono");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("0000-0000", funcionarioService.buscarPorId(id).get().getTelefono());
    }

    @Test
    void rechazaTelefonoVacioSinModificarElFuncionario() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        funcionarioService.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        CuentaControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "   ");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("0000-0000", funcionarioService.buscarPorId(id).get().getTelefono());
    }

    private static String idUnico() {
        return "IT-" + System.nanoTime();
    }

    private static CuentaControl construirControl() {
        return new CuentaControl(
                new Frame(),
                ServiceFactory.obtenerAutenticacionService(),
                funcionarioService
        );
    }

    private static void invocarGuardarMiCuenta(CuentaControl control, PanelCuenta panelCuenta) {
        control.guardarMiCuenta(panelCuenta);
    }
}
