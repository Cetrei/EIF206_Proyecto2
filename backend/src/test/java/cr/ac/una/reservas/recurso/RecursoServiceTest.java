package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.persistence.falso.ReservaDaoFalso;
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

class RecursoServiceTest {

    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private ReservaDaoFalso reservaDao;
    private RecursoService recursoService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        reservaDao = new ReservaDaoFalso();
        recursoService = new RecursoServiceImpl(recursoDao, categoriaDao, reservaDao);

        categoriaSala = new Categoria(1, "Sala para 10 personas");
        categoriaDao.insertar(categoriaSala);
    }

    @Test
    void creaRecursoConCategoriaValida() {
        Recurso recurso = new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso");

        recursoService.crear(recurso);

        assertTrue(recursoDao.buscarPorId("34343").isPresent());
    }

    @Test
    void rechazaCrearRecursoSinId() {
        Recurso recurso = new Recurso(" ", categoriaSala.getId(), "Sala 1 primer piso");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(recurso));
    }

    @Test
    void rechazaCrearRecursoConIdDuplicado() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        Recurso duplicado = new Recurso("34343", categoriaSala.getId(), "Otra descripcion");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(duplicado));
    }

    @Test
    void rechazaCrearRecursoConCategoriaInexistente() {
        Recurso recurso = new Recurso("34343", 999999, "Sala 1 primer piso");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(recurso));
    }

    @Test
    void modificaRecursoExistente() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        Recurso modificado = new Recurso("34343", categoriaSala.getId(), "Sala 1 segundo piso");

        recursoService.modificar(modificado);

        assertEquals("Sala 1 segundo piso", recursoDao.buscarPorId("34343").get().getDescripcion());
    }

    @Test
    void rechazaModificarRecursoInexistente() {
        Recurso recurso = new Recurso("999999", categoriaSala.getId(), "No existe");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.modificar(recurso));
    }

    @Test
    void listaRecursosPorCategoriaConCategoriaResuelta() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));

        List<Recurso> resultado = recursoService.listarPorCategoria(categoriaSala.getId());

        assertEquals(1, resultado.size());
        assertEquals(categoriaSala.getId(), resultado.get(0).getCategoria().getId());
    }

    @Test
    void filtraPorCategoriaYDescripcion() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        recursoDao.insertar(new Recurso("45238", categoriaSala.getId(), "Sala 2 segundo piso"));

        List<Recurso> resultado = recursoService.filtrar(categoriaSala.getId(), "primer");

        assertEquals(1, resultado.size());
        assertEquals("34343", resultado.get(0).getId());
    }

    @Test
    void eliminaRecurso() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));

        recursoService.eliminar("34343");

        assertTrue(recursoDao.buscarPorId("34343").isEmpty());
    }

    @Test
    void rechazaEliminarRecursoInexistente() {
        assertThrows(ReglaDeNegocioException.class, () -> recursoService.eliminar("999999"));
    }

    @Test
    void rechazaEliminarRecursoAsignadoAUnaReservaActiva() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        reservaDao.insertar(reservaConRecurso(1, "34343", EstadoReserva.ACTIVA));

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.eliminar("34343"));
        assertTrue(recursoDao.buscarPorId("34343").isPresent());
    }

    @Test
    void permiteEliminarRecursoCuyaReservaFueCancelada() {
        recursoDao.insertar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        reservaDao.insertar(reservaConRecurso(1, "34343", EstadoReserva.CANCELADA));

        recursoService.eliminar("34343");

        assertTrue(recursoDao.buscarPorId("34343").isEmpty());
    }

    @Test
    void filtraTambienPorNumeroDeActivo() {
        recursoDao.insertar(new Recurso("238715", categoriaSala.getId(), "Laptop principal"));
        recursoDao.insertar(new Recurso("45238", categoriaSala.getId(), "Laptop secundaria"));

        List<Recurso> resultado = recursoService.filtrar(null, "238715");

        assertEquals(1, resultado.size());
        assertEquals("238715", resultado.get(0).getId());
    }

    private Reserva reservaConRecurso(Integer id, String idRecurso, EstadoReserva estado) {
        DatosNuevaReserva datos = new DatosNuevaReserva(
                id,
                "111",
                "Reunion de trabajo",
                LocalDate.of(2026, 8, 5),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                List.of(categoriaSala.getId())
        );
        Reserva reserva = new Reserva(datos);
        reserva.setIdsRecursosAsignados(List.of(idRecurso));
        reserva.setEstado(estado);
        return reserva;
    }
}
