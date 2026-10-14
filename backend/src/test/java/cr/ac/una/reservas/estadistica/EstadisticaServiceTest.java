package cr.ac.una.reservas.estadistica;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.persistence.falso.ReservaDaoFalso;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.reserva.DatosNuevaReserva;
import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.reserva.Reserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadisticaServiceTest {

    private ReservaDaoFalso reservaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private EstadisticaService estadisticaService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        reservaDao = new ReservaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        estadisticaService = new EstadisticaServiceImpl(reservaDao, recursoDao, categoriaDao);

        categoriaSala = new Categoria(1, "Sala para 10 personas");
        categoriaDao.insertar(categoriaSala);
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
    }

    @Test
    void contabilizaRecursosReservadosEnElPeriodo() {
        LocalDate fecha = LocalDate.of(2026, 8, 5);
        guardarReservaActiva(1, fecha, List.of("34343"));

        List<EstadisticaCategoria> resultado = estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(1, resultado.size());
        assertEquals(categoriaSala.getId(), resultado.get(0).getCategoria().getId());
        assertEquals(1L, resultado.get(0).getCantidad());
    }

    @Test
    void ignoraReservasFueraDelPeriodo() {
        guardarReservaActiva(1, LocalDate.of(2026, 8, 20), List.of("34343"));

        List<EstadisticaCategoria> resultado = estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertTrue(resultado.isEmpty());
    }

    @Test
    void rechazaRangoDeRecursosSinFechaDesde() {
        assertThrows(ReglaDeNegocioException.class,
                () -> estadisticaService.recursosReservadosEnPeriodo(null, LocalDate.of(2026, 8, 13)));
    }

    @Test
    void rechazaRangoDeRecursosConHastaAntesDeDesde() {
        assertThrows(ReglaDeNegocioException.class, () -> estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 13), LocalDate.of(2026, 8, 1)
        ));
    }

    @Test
    void agrupaActividadesPorSemanaEnElPeriodo() {
        LocalDate lunes = LocalDate.of(2026, 8, 3);
        guardarReservaActiva(1, lunes, List.of("34343"));
        guardarReservaActiva(2, lunes.plusDays(1), List.of("34343"));

        List<EstadisticaSemana> resultado = estadisticaService.actividadesPorSemanaEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(1, resultado.size());
        assertEquals(lunes, resultado.get(0).getInicioSemana());
        assertEquals(2L, resultado.get(0).getCantidad());
    }

    @Test
    void ordenaEstadisticasDeActividadesPorSemanaAscendente() {
        LocalDate primeraSemana = LocalDate.of(2026, 8, 3);
        LocalDate segundaSemana = LocalDate.of(2026, 8, 10);
        guardarReservaActiva(1, segundaSemana, List.of("34343"));
        guardarReservaActiva(2, primeraSemana, List.of("34343"));

        List<EstadisticaSemana> resultado = estadisticaService.actividadesPorSemanaEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(primeraSemana, resultado.get(0).getInicioSemana());
        assertEquals(segundaSemana, resultado.get(1).getInicioSemana());
    }

    @Test
    void rechazaRangoDeActividadesConFechasNulas() {
        assertThrows(ReglaDeNegocioException.class,
                () -> estadisticaService.actividadesPorSemanaEnPeriodo(null, null));
    }

    @Test
    void ignoraLasReservasCanceladasEnAmbasEstadisticas() {
        LocalDate fecha = LocalDate.of(2026, 8, 5);
        Reserva cancelada = guardarReservaActiva(1, fecha, List.of("34343"));
        cancelada.setEstado(EstadoReserva.CANCELADA);
        reservaDao.actualizar(cancelada);

        assertTrue(estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        ).isEmpty());
        assertTrue(estadisticaService.actividadesPorSemanaEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        ).isEmpty());
    }

    @Test
    void incluyeLasReservasJustoEnLosLimitesDelPeriodo() {
        guardarReservaActiva(1, LocalDate.of(2026, 8, 1), List.of("34343"));
        guardarReservaActiva(2, LocalDate.of(2026, 8, 13), List.of("34343"));

        List<EstadisticaCategoria> resultado = estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getCantidad());
    }

    @Test
    void ignoraRecursosAsignadosQueYaNoExisten() {
        guardarReservaActiva(1, LocalDate.of(2026, 8, 5), List.of("recurso-borrado"));

        assertTrue(estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        ).isEmpty());
    }

    private Reserva guardarReservaActiva(Integer id, LocalDate fecha, List<String> idsRecursos) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(id);
        datos.setIdFuncionario("111");
        datos.setActividad("Reunion de trabajo");
        datos.setFecha(fecha);
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(List.of(categoriaSala.getId()));

        Reserva reserva = new Reserva(datos);
        reserva.setIdsRecursosAsignados(idsRecursos);
        reservaDao.insertar(reserva);
        return reserva;
    }
}
