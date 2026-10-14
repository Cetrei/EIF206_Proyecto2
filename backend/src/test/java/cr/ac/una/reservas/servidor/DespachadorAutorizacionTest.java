package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.autenticacion.RolUsuario;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaServiceImpl;
import cr.ac.una.reservas.excepcion.AutorizacionException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.funcionario.FuncionarioServiceImpl;
import cr.ac.una.reservas.persistence.memoria.CategoriaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.FuncionarioDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.RecursoDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.ReservaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Peticion;
import cr.ac.una.reservas.protocol.Respuesta;
import cr.ac.una.reservas.protocol.Rutas;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.reserva.DatosNuevaReserva;
import cr.ac.una.reservas.reserva.ReservaServiceImpl;
import cr.ac.una.reservas.reserva.ResultadoReserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DespachadorAutorizacionTest {

    private FuncionarioDaoEnMemoriaTemporal funcionarios;
    private Despachador despachador;
    private SesionCliente administrador;
    private SesionCliente ana;
    private SesionCliente beto;
    private SesionCliente sinSesion;
    private int categoriaSala;
    private long consecutivo;

    @BeforeEach
    void prepararDespachador() {
        CategoriaDaoEnMemoriaTemporal categorias = new CategoriaDaoEnMemoriaTemporal();
        RecursoDaoEnMemoriaTemporal recursos = new RecursoDaoEnMemoriaTemporal();
        ReservaDaoEnMemoriaTemporal reservas = new ReservaDaoEnMemoriaTemporal();
        funcionarios = new FuncionarioDaoEnMemoriaTemporal();

        Categoria sala = categorias.insertar(new Categoria(1, "Sala"));
        categoriaSala = sala.getId();
        recursos.insertar(new Recurso("R1", categoriaSala, "Sala 1"));
        recursos.insertar(new Recurso("R2", categoriaSala, "Sala 2"));
        funcionarios.insertar(new Funcionario("111", "111", "Ana", "8888-0000"));
        funcionarios.insertar(new Funcionario("222", "222", "Beto", "7777-0000"));

        despachador = new Despachador();
        RutasCategoria.registrar(despachador, new CategoriaServiceImpl(categorias, recursos));
        RutasFuncionario.registrar(despachador, new FuncionarioServiceImpl(funcionarios, reservas));
        RutasReserva.registrar(despachador, new ReservaServiceImpl(reservas, recursos, categorias));

        administrador = sesionDe(new Administrador("admin", "123"));
        ana = sesionDe(new Funcionario("111", "111", "Ana", "8888-0000"));
        beto = sesionDe(new Funcionario("222", "222", "Beto", "7777-0000"));
        sinSesion = new SesionCliente();
    }

    @Test
    void rechazaLasRutasProtegidasSinSesion() {
        Respuesta respuesta = enviar(sinSesion, Accion.GET, Rutas.CATEGORIAS, null);

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(SesionNoIniciadaException.class, respuesta.getError());
    }

    @Test
    void elFuncionarioNoPuedeCrearCategorias() {
        Respuesta respuesta = enviar(ana, Accion.POST, Rutas.CATEGORIAS, "Proyector");

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(AutorizacionException.class, respuesta.getError());
    }

    @Test
    void elAdministradorCreaCategorias() {
        Respuesta respuesta = enviar(administrador, Accion.POST, Rutas.CATEGORIAS, "Proyector");

        assertTrue(respuesta.isExitosa());
    }

    @Test
    void elFuncionarioPuedeListarCategorias() {
        Respuesta respuesta = enviar(ana, Accion.GET, Rutas.CATEGORIAS, null);

        assertTrue(respuesta.isExitosa());
    }

    @Test
    void elFuncionarioNoPuedeListarFuncionarios() {
        Respuesta respuesta = enviar(ana, Accion.GET, Rutas.FUNCIONARIOS, null);

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(AutorizacionException.class, respuesta.getError());
    }

    @Test
    void elFuncionarioNoPuedeModificarLaCuentaDeOtro() {
        Funcionario deBeto = new Funcionario("222", "222", "Beto", "1111-1111");

        Respuesta respuesta = enviar(ana, Accion.PUT, Rutas.FUNCIONARIOS, deBeto);

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(AutorizacionException.class, respuesta.getError());
        assertEquals("7777-0000", funcionarios.buscarPorId("222").orElseThrow().getTelefono());
    }

    @Test
    void elFuncionarioModificaSuPropiaCuentaSinPoderCambiarseElRol() {
        Funcionario propia = new Funcionario("111", "111", "Ana", "2222-2222");
        propia.setRol(RolUsuario.ADMINISTRADOR);

        Respuesta respuesta = enviar(ana, Accion.PUT, Rutas.FUNCIONARIOS, propia);

        assertTrue(respuesta.isExitosa());
        Funcionario guardado = funcionarios.buscarPorId("111").orElseThrow();
        assertEquals("2222-2222", guardado.getTelefono());
        assertEquals(RolUsuario.FUNCIONARIO, guardado.getRol());
    }

    @Test
    void elAdministradorNoPuedeReservar() {
        Respuesta respuesta = enviar(administrador, Accion.POST, Rutas.RESERVAS, datosDeReserva("admin", 8));

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(AutorizacionException.class, respuesta.getError());
    }

    @Test
    void laReservaSeGuardaConElFuncionarioDeLaSesionAunqueElClienteMandeOtro() {
        Respuesta respuesta = enviar(ana, Accion.POST, Rutas.RESERVAS, datosDeReserva("222", 8));

        assertTrue(respuesta.isExitosa());
        ResultadoReserva resultado = (ResultadoReserva) respuesta.getCuerpo();
        assertEquals("111", resultado.getReserva().getIdFuncionario());
    }

    @Test
    void elFuncionarioNoPuedeCancelarLaReservaDeOtro() {
        Integer idDeAna = reservarComo(ana, 8);

        Respuesta respuesta = enviar(beto, Accion.DELETE, Rutas.RESERVAS, idDeAna);

        assertFalse(respuesta.isExitosa());
        assertInstanceOf(AutorizacionException.class, respuesta.getError());
    }

    @Test
    void elFuncionarioCancelaSuPropiaReserva() {
        Integer idDeAna = reservarComo(ana, 8);

        Respuesta respuesta = enviar(ana, Accion.DELETE, Rutas.RESERVAS, idDeAna);

        assertTrue(respuesta.isExitosa());
    }

    @Test
    void elAdministradorCancelaLaReservaDeCualquiera() {
        Integer idDeAna = reservarComo(ana, 8);

        Respuesta respuesta = enviar(administrador, Accion.DELETE, Rutas.RESERVAS, idDeAna);

        assertTrue(respuesta.isExitosa());
    }

    @Test
    void cancelarUnaReservaInexistenteDaUnErrorDeNegocioYNoDePermiso() {
        Respuesta respuesta = enviar(ana, Accion.DELETE, Rutas.RESERVAS, 999);

        assertFalse(respuesta.isExitosa());
        assertFalse(respuesta.getError() instanceof AutorizacionException);
    }

    private Integer reservarComo(SesionCliente sesion, int horaInicio) {
        Respuesta respuesta = enviar(sesion, Accion.POST, Rutas.RESERVAS, datosDeReserva(null, horaInicio));
        return ((ResultadoReserva) respuesta.getCuerpo()).getReserva().getId();
    }

    private DatosNuevaReserva datosDeReserva(String idFuncionario, int horaInicio) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setIdFuncionario(idFuncionario);
        datos.setActividad("Reunion");
        datos.setFecha(LocalDate.now().plusDays(1));
        datos.setHoraInicio(LocalTime.of(horaInicio, 0));
        datos.setHoraFin(LocalTime.of(horaInicio + 1, 0));
        datos.setIdsCategoriasRequeridas(List.of(categoriaSala));
        return datos;
    }

    private Respuesta enviar(SesionCliente sesion, Accion accion, String ruta, Serializable cuerpo) {
        return despachador.despachar(new Peticion(++consecutivo, accion, ruta, cuerpo), sesion);
    }

    private static SesionCliente sesionDe(Usuario usuario) {
        SesionCliente sesion = new SesionCliente();
        sesion.iniciar(usuario);
        return sesion;
    }
}
