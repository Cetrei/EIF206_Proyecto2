package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.ia.ConfiguracionIa;
import cr.ac.una.reservas.persistence.PersistenciaException;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.reserva.Reserva;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DaoMySqlIT {
    private static final String ID_ADMINISTRADOR = "IT-ADMIN";
    private static final String ID_FUNCIONARIO = "IT-FUNC";
    private static final String ID_RECURSO_A = "IT-REC-A";
    private static final String ID_RECURSO_B = "IT-REC-B";
    private static final String DESCRIPCION_CATEGORIA = "IT categoria";

    private ConexionBd conexion;
    private DaoMySqlAdministrador administradores;
    private DaoMySqlFuncionario funcionarios;
    private DaoMySqlCategoria categorias;
    private DaoMySqlRecurso recursos;
    private DaoMySqlReserva reservas;
    private DaoMySqlConfiguracionIa configuraciones;
    private Optional<ConfiguracionIa> configuracionPrevia = Optional.empty();

    @BeforeEach
    void conectar() {
        conexion = new ConexionBd(
                System.getProperty("bd.host", "localhost"),
                Integer.parseInt(System.getProperty("bd.puerto", "3306")),
                System.getProperty("bd.base", "reservas_test"),
                System.getProperty("bd.usuario", "root"),
                System.getProperty("bd.clave", "")
        );
        assumeTrue(hayBaseConEsquema(), "No hay una base MySQL con el esquema de base_datos.sql");
        administradores = new DaoMySqlAdministrador(conexion);
        funcionarios = new DaoMySqlFuncionario(conexion);
        categorias = new DaoMySqlCategoria(conexion);
        recursos = new DaoMySqlRecurso(conexion);
        reservas = new DaoMySqlReserva(conexion);
        configuraciones = new DaoMySqlConfiguracionIa(conexion);
        configuracionPrevia = configuraciones.buscarPorId(ConfiguracionIa.ID_UNICO);
        limpiar();
    }

    @AfterEach
    void restaurar() {
        if (reservas == null) {
            return;
        }
        limpiar();
        configuraciones.eliminar(ConfiguracionIa.ID_UNICO);
        configuracionPrevia.ifPresent(configuraciones::insertar);
    }

    @Test
    void deberiaAdministrarUsuariosSinMezclarRoles() {
        administradores.insertar(new Administrador(ID_ADMINISTRADOR, "123"));
        funcionarios.insertar(new Funcionario(ID_FUNCIONARIO, "abc", "Ana Perez", "8888-0000"));

        assertEquals("123", administradores.buscarPorId(ID_ADMINISTRADOR).orElseThrow().getClave());
        assertTrue(administradores.buscarPorId(ID_FUNCIONARIO).isEmpty());
        assertTrue(funcionarios.buscarPorId(ID_ADMINISTRADOR).isEmpty());
        assertTrue(administradores.listarTodos().stream().noneMatch(a -> ID_FUNCIONARIO.equals(a.getId())));
        assertTrue(funcionarios.listarTodos().stream().noneMatch(f -> ID_ADMINISTRADOR.equals(f.getId())));
        assertThrows(
                PersistenciaException.class,
                () -> funcionarios.insertar(new Funcionario(ID_FUNCIONARIO, "x", "Otro", "1"))
        );

        Funcionario cambiado = new Funcionario(ID_FUNCIONARIO, "nueva", "Ana Perez Mora", "7777-1111");
        assertTrue(funcionarios.actualizar(cambiado));
        Funcionario leido = funcionarios.buscarPorId(ID_FUNCIONARIO).orElseThrow();
        assertEquals("nueva", leido.getClave());
        assertEquals("Ana Perez Mora", leido.getNombre());
        assertEquals("7777-1111", leido.getTelefono());
        assertEquals(1, funcionarios.buscarPorNombre("perez mora").size());

        assertTrue(funcionarios.eliminar(ID_FUNCIONARIO));
        assertFalse(funcionarios.eliminar(ID_FUNCIONARIO));
        assertTrue(administradores.eliminar(ID_ADMINISTRADOR));
    }

    @Test
    void deberiaAdministrarCategoriasYRecursos() {
        Categoria categoria = categorias.insertar(new Categoria(null, DESCRIPCION_CATEGORIA));
        assertNotNull(categoria.getId());
        assertEquals(DESCRIPCION_CATEGORIA, categorias.buscarPorId(categoria.getId()).orElseThrow().getDescripcion());
        assertTrue(categorias.buscarPorDescripcion("IT CATEGORIA").stream()
                .anyMatch(c -> c.getId().equals(categoria.getId())));

        categoria.setDescripcion("IT categoria editada");
        assertTrue(categorias.actualizar(categoria));
        assertEquals("IT categoria editada", categorias.buscarPorId(categoria.getId()).orElseThrow().getDescripcion());

        recursos.insertar(new Recurso(ID_RECURSO_A, categoria.getId(), "Proyector IT"));
        assertThrows(
                PersistenciaException.class,
                () -> recursos.insertar(new Recurso(ID_RECURSO_A, categoria.getId(), "Repetido"))
        );
        assertEquals(1, recursos.listarPorCategoria(categoria.getId()).size());
        assertEquals("Proyector IT", recursos.buscarPorId(ID_RECURSO_A).orElseThrow().getDescripcion());

        assertTrue(recursos.eliminar(ID_RECURSO_A));
        assertTrue(categorias.eliminar(categoria.getId()));
        assertFalse(categorias.eliminar(categoria.getId()));
    }

    @Test
    void deberiaGuardarReservasConSusRecursos() {
        funcionarios.insertar(new Funcionario(ID_FUNCIONARIO, "abc", "Ana Perez", "8888-0000"));
        Categoria categoria = categorias.insertar(new Categoria(null, DESCRIPCION_CATEGORIA));
        recursos.insertar(new Recurso(ID_RECURSO_A, categoria.getId(), "Recurso A"));
        recursos.insertar(new Recurso(ID_RECURSO_B, categoria.getId(), "Recurso B"));

        Reserva guardada = reservas.insertar(reservaDePrueba("Reunion IT", List.of(ID_RECURSO_A)));

        assertNotNull(guardada.getId());
        Reserva leida = reservas.buscarPorId(guardada.getId()).orElseThrow();
        assertEquals("Reunion IT", leida.getActividad());
        assertEquals(LocalTime.of(10, 30), leida.getHoraFin());
        assertEquals(List.of(ID_RECURSO_A), leida.getIdsRecursosAsignados());
        assertEquals(List.of(categoria.getId()), leida.getIdsCategoriasRequeridas());
        assertEquals(1, reservas.listarPorFuncionario(ID_FUNCIONARIO).size());

        leida.setEstado(EstadoReserva.CANCELADA);
        leida.setIdsRecursosAsignados(List.of(ID_RECURSO_B));
        assertTrue(reservas.actualizar(leida));
        Reserva modificada = reservas.buscarPorId(guardada.getId()).orElseThrow();
        assertEquals(EstadoReserva.CANCELADA, modificada.getEstado());
        assertEquals(List.of(ID_RECURSO_B), modificada.getIdsRecursosAsignados());
        assertTrue(reservas.listarTodos().stream().anyMatch(r -> r.getId().equals(guardada.getId())));

        assertTrue(reservas.eliminar(guardada.getId()));
        assertTrue(reservas.buscarPorId(guardada.getId()).isEmpty());
    }

    @Test
    void deberiaAgruparEstadisticasConSql() {
        funcionarios.insertar(new Funcionario(ID_FUNCIONARIO, "abc", "Ana Perez", "8888-0000"));
        Categoria categoria = categorias.insertar(new Categoria(null, DESCRIPCION_CATEGORIA));
        recursos.insertar(new Recurso(ID_RECURSO_A, categoria.getId(), "Recurso A"));
        recursos.insertar(new Recurso(ID_RECURSO_B, categoria.getId(), "Recurso B"));
        guardarEn(LocalDate.of(2031, 3, 10), EstadoReserva.ACTIVA, List.of(ID_RECURSO_A, ID_RECURSO_B));
        guardarEn(LocalDate.of(2031, 3, 12), EstadoReserva.ACTIVA, List.of(ID_RECURSO_A));
        guardarEn(LocalDate.of(2031, 3, 18), EstadoReserva.ACTIVA, List.of(ID_RECURSO_A));
        guardarEn(LocalDate.of(2031, 3, 11), EstadoReserva.CANCELADA, List.of(ID_RECURSO_B));

        var porRecurso = reservas.contarRecursosReservados(LocalDate.of(2031, 3, 3), LocalDate.of(2031, 3, 16));
        assertEquals(2L, porRecurso.get(ID_RECURSO_A));
        assertEquals(1L, porRecurso.get(ID_RECURSO_B));

        var porSemana = reservas.contarActividadesPorSemana(LocalDate.of(2031, 3, 3), LocalDate.of(2031, 3, 23));
        assertEquals(2L, porSemana.get(LocalDate.of(2031, 3, 10)));
        assertEquals(1L, porSemana.get(LocalDate.of(2031, 3, 17)));
    }

    private void guardarEn(LocalDate fecha, EstadoReserva estado, List<String> idsRecursos) {
        Reserva reserva = reservaDePrueba("Estadistica IT", idsRecursos);
        reserva.setFecha(fecha);
        reserva.setEstado(estado);
        reservas.insertar(reserva);
    }

    @Test
    void deberiaRechazarUnaReservaConRecursoInexistenteSinDejarRastro() {
        funcionarios.insertar(new Funcionario(ID_FUNCIONARIO, "abc", "Ana Perez", "8888-0000"));
        Reserva reserva = reservaDePrueba("Sin recurso valido", List.of("IT-NO-EXISTE"));

        assertThrows(PersistenciaException.class, () -> reservas.insertar(reserva));
        assertTrue(reservas.listarPorFuncionario(ID_FUNCIONARIO).isEmpty());
    }

    @Test
    void deberiaConservarUnaSolaConfiguracionDeIa() {
        configuraciones.eliminar(ConfiguracionIa.ID_UNICO);
        assertTrue(configuraciones.buscarPorId(ConfiguracionIa.ID_UNICO).isEmpty());
        assertFalse(configuraciones.actualizar(new ConfiguracionIa("clave", "modelo")));

        configuraciones.insertar(new ConfiguracionIa("clave-1", "modelo-1"));
        configuraciones.insertar(new ConfiguracionIa("clave-2", "modelo-2"));
        assertEquals(1, configuraciones.listarTodos().size());
        assertTrue(configuraciones.actualizar(new ConfiguracionIa("clave-3", "modelo-3")));

        ConfiguracionIa leida = configuraciones.buscarPorId(ConfiguracionIa.ID_UNICO).orElseThrow();
        assertEquals("clave-3", leida.getApiKey());
        assertEquals("modelo-3", leida.getModelo());
    }

    private Reserva reservaDePrueba(String actividad, List<String> idsRecursos) {
        Reserva reserva = new Reserva();
        reserva.setIdFuncionario(ID_FUNCIONARIO);
        reserva.setActividad(actividad);
        reserva.setFecha(LocalDate.now().plusDays(3));
        reserva.setHoraInicio(LocalTime.of(9, 0));
        reserva.setHoraFin(LocalTime.of(10, 30));
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setIdsRecursosAsignados(idsRecursos);
        return reserva;
    }

    private boolean hayBaseConEsquema() {
        String consulta = "SELECT 1 FROM usuario, funcionario, categoria, recurso, reserva, reserva_recurso, "
                + "configuracion_ia LIMIT 1";
        try (Connection enlace = conexion.obtener();
             PreparedStatement sentencia = enlace.prepareStatement(consulta)) {
            sentencia.executeQuery().close();
            return true;
        } catch (SQLException | PersistenciaException excepcion) {
            return false;
        }
    }

    private void limpiar() {
        ejecutar("DELETE FROM reserva_recurso WHERE id_recurso LIKE 'IT-%' OR id_reserva IN "
                + "(SELECT id FROM (SELECT id FROM reserva WHERE id_funcionario LIKE 'IT-%') AS propias)");
        ejecutar("DELETE FROM reserva WHERE id_funcionario LIKE 'IT-%'");
        ejecutar("DELETE FROM recurso WHERE id LIKE 'IT-%'");
        ejecutar("DELETE FROM categoria WHERE descripcion LIKE 'IT categoria%'");
        ejecutar("DELETE FROM funcionario WHERE id LIKE 'IT-%'");
        ejecutar("DELETE FROM usuario WHERE id LIKE 'IT-%'");
    }

    private void ejecutar(String sql) {
        try (Connection enlace = conexion.obtener();
             PreparedStatement sentencia = enlace.prepareStatement(sql)) {
            sentencia.executeUpdate();
        } catch (SQLException excepcion) {
            throw new PersistenciaException("No se pudo limpiar la base de pruebas", excepcion);
        }
    }
}
