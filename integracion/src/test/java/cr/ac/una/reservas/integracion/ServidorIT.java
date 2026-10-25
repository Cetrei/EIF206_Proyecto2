package cr.ac.una.reservas.integracion;

import cr.ac.una.reservas.persistence.falso.ModoMemoria;
import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ErrorInternoException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.protocol.AlertaReserva;
import cr.ac.una.reservas.protocol.Notificacion;
import cr.ac.una.reservas.protocol.SesionTerminada;
import cr.ac.una.reservas.proxy.ConexionBackend;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.reserva.DatosNuevaReserva;
import cr.ac.una.reservas.reserva.ResultadoReserva;
import cr.ac.una.reservas.proxy.ProveedorRemoto;
import cr.ac.una.reservas.servidor.RutasBackend;
import cr.ac.una.reservas.servidor.ServidorBackend;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServidorIT {
    static {
        ModoMemoria.activar();
    }

    private static ServidorBackend servidor;

    private final List<ConexionBackend> conexiones = new ArrayList<>();

    @BeforeAll
    static void iniciarServidor() throws IOException {
        servidor = new ServidorBackend(RutasBackend.crearDespachador());
        servidor.iniciar(0);
    }

    @AfterAll
    static void detenerServidor() {
        servidor.close();
    }

    @AfterEach
    void cerrarConexiones() {
        conexiones.forEach(ConexionBackend::close);
    }

    private ConexionBackend conectar(ServidorBackend destino, BlockingQueue<Notificacion> recibidas) {
        ConexionBackend conexion = new ConexionBackend("localhost", destino.getPuertoLocal(), recibidas::add);
        conexion.conectar();
        conexiones.add(conexion);
        return conexion;
    }

    private ProveedorRemoto sesionAdministrador(ConexionBackend conexion) {
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);
        proveedor.autenticacion().autenticar("admin", "123");
        return proveedor;
    }

    @Test
    void peticionYRespuestaViajanPorElSocket() {
        ConexionBackend conexion = conectar(servidor, new LinkedBlockingQueue<>());
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);

        Usuario usuario = proveedor.autenticacion().autenticar("admin", "123");
        Categoria creada = proveedor.categoria().crear("Categoria de red " + System.nanoTime());

        assertInstanceOf(Administrador.class, usuario);
        assertNotNull(creada.getId());
        assertTrue(proveedor.categoria().listarTodas().stream().anyMatch(c -> creada.getId().equals(c.getId())));

        proveedor.categoria().eliminar(creada.getId());
        assertFalse(proveedor.categoria().listarTodas().stream().anyMatch(c -> creada.getId().equals(c.getId())));
    }

    @Test
    void sinSesionSoloSePermiteIniciarSesionOCambiarClave() {
        ConexionBackend conexion = conectar(servidor, new LinkedBlockingQueue<>());
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);

        assertThrows(SesionNoIniciadaException.class, () -> proveedor.categoria().listarTodas());
    }

    @Test
    void elErrorDeNegocioLlegaConSuClaseYSinSerErrorInterno() {
        ConexionBackend conexion = conectar(servidor, new LinkedBlockingQueue<>());
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);

        ReservaAppException error = assertThrows(
                ReservaAppException.class,
                () -> proveedor.autenticacion().autenticar("admin", "clave-incorrecta")
        );

        assertFalse(error instanceof ErrorInternoException);
        assertFalse(error.getMessage().isBlank());
        assertNull(error.getCause());
    }

    @Test
    void laNotificacionLlegaSoloALasConexionesConSesion() throws InterruptedException {
        BlockingQueue<Notificacion> conSesion = new LinkedBlockingQueue<>();
        BlockingQueue<Notificacion> sinSesion = new LinkedBlockingQueue<>();
        sesionAdministrador(conectar(servidor, conSesion));
        conectar(servidor, sinSesion);
        AlertaReserva alerta = new AlertaReserva(
                7, "Juan Mora", "Taller", LocalDate.of(2026, 10, 21),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of("Laptop #1")
        );

        servidor.difundir(alerta);

        Notificacion recibida = conSesion.poll(5, TimeUnit.SECONDS);
        AlertaReserva recibidaComoAlerta = assertInstanceOf(AlertaReserva.class, recibida);
        assertEquals(7, recibidaComoAlerta.getIdReserva());
        assertEquals("Taller", recibidaComoAlerta.getActividad());
        assertNull(sinSesion.poll(300, TimeUnit.MILLISECONDS));
    }

    @Test
    void sinSesionActivaLaNotificacionYaNoLlega() throws InterruptedException {
        BlockingQueue<Notificacion> recibidas = new LinkedBlockingQueue<>();
        ProveedorRemoto proveedor = sesionAdministrador(conectar(servidor, recibidas));
        proveedor.cerrarSesion();
        AlertaReserva alerta = new AlertaReserva(
                8, "Juan Mora", "Taller", LocalDate.of(2026, 10, 21),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of()
        );

        servidor.difundir(alerta);

        assertNull(recibidas.poll(300, TimeUnit.MILLISECONDS));
    }

    @Test
    void laReservaNuevaSeNotificaATodosMenosAlAutor() throws Exception {
        ServidorBackend servidorPropio = new ServidorBackend(RutasBackend.crearDespachador());
        RutasBackend.instalarDifusor(servidorPropio);
        servidorPropio.iniciar(0);
        BlockingQueue<Notificacion> alertasAdmin = new LinkedBlockingQueue<>();
        BlockingQueue<Notificacion> alertasAutor = new LinkedBlockingQueue<>();
        ProveedorRemoto admin = new ProveedorRemoto(conectar(servidorPropio, alertasAdmin));
        admin.autenticacion().autenticar("admin", "123");
        ProveedorRemoto autor = new ProveedorRemoto(conectar(servidorPropio, alertasAutor));
        autor.autenticacion().autenticar("111", "111");
        long marca = System.nanoTime();
        Categoria categoria = admin.categoria().crear("Categoria alerta " + marca);
        admin.recurso().crear(new Recurso("R-" + marca, categoria.getId(), "Sala alerta " + marca));
        DatosNuevaReserva datos = new DatosNuevaReserva(
                null, "111", "Actividad " + marca, LocalDate.now().plusDays(30),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(categoria.getId())
        );

        ResultadoReserva resultado = autor.reserva().intentarReservar(datos);

        assertTrue(resultado.isExitoso());
        AlertaReserva alerta = assertInstanceOf(AlertaReserva.class, alertasAdmin.poll(5, TimeUnit.SECONDS));
        assertEquals(resultado.getReserva().getId(), alerta.getIdReserva());
        assertEquals("Actividad " + marca, alerta.getActividad());
        assertEquals(List.of("Sala alerta " + marca), alerta.getDescripcionesRecursos());
        assertNull(alertasAutor.poll(300, TimeUnit.MILLISECONDS));
        servidorPropio.close();
    }

    @Test
    void alEliminarUnFuncionarioSuSesionRecibeSesionTerminadaYSeCierra() throws Exception {
        BlockingQueue<Notificacion> avisosDelFuncionario = new LinkedBlockingQueue<>();
        BlockingQueue<Notificacion> avisosDelOtro = new LinkedBlockingQueue<>();
        ProveedorRemoto admin = sesionAdministrador(conectar(servidor, new LinkedBlockingQueue<>()));
        String id = String.valueOf(System.nanoTime() % 1_000_000_000L);
        admin.funcionario().crear(new Funcionario(id, "clave", "Funcionario temporal", "8888-0000"));
        ProveedorRemoto afectado = new ProveedorRemoto(conectar(servidor, avisosDelFuncionario));
        afectado.autenticacion().autenticar(id, id);
        ProveedorRemoto otro = new ProveedorRemoto(conectar(servidor, avisosDelOtro));
        otro.autenticacion().autenticar("111", "111");

        admin.funcionario().eliminar(id);

        SesionTerminada aviso = assertInstanceOf(SesionTerminada.class, avisosDelFuncionario.poll(5, TimeUnit.SECONDS));
        assertFalse(aviso.getMensaje().isBlank());
        assertNull(avisosDelOtro.poll(300, TimeUnit.MILLISECONDS));
        assertThrows(SesionNoIniciadaException.class, () -> afectado.categoria().listarTodas());
        assertNotNull(otro.categoria().listarTodas());
    }

    @Test
    void cuandoElServidorCaeElClienteRecibeConexionException() throws Exception {
        ServidorBackend servidorPropio = new ServidorBackend(RutasBackend.crearDespachador());
        servidorPropio.iniciar(0);
        ConexionBackend conexion = conectar(servidorPropio, new LinkedBlockingQueue<>());
        ProveedorRemoto proveedor = sesionAdministrador(conexion);
        CountDownLatch perdida = new CountDownLatch(1);
        conexion.alPerderConexion(perdida::countDown);

        servidorPropio.close();

        assertTrue(perdida.await(5, TimeUnit.SECONDS));
        assertFalse(conexion.estaConectado());
        assertThrows(ConexionException.class, () -> proveedor.categoria().listarTodas());
    }
}
