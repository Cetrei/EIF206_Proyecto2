
package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.persistence.falso.ReservaDaoFalso;
import cr.ac.una.reservas.recurso.Recurso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaCasosAdicionalesTest {

    private ReservaDaoFalso reservaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private ReservaService servicio;

    private final LocalDate fecha = LocalDate.now().plusDays(2);

    @BeforeEach
    void prepararPruebas() {
        reservaDao = new ReservaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();

        servicio = new ReservaServiceImpl(
                reservaDao, recursoDao, categoriaDao
        );

        categoriaDao.insertar(new Categoria(1, "Salas"));
        recursoDao.insertar(new Recurso(
                "S1", 1, "Sala principal"
        ));
    }

    private DatosNuevaReserva crearDatos(
            String funcionario,
            int inicio,
            int fin
    ) {
        return new DatosNuevaReserva(
                null,
                funcionario,
                "Reunion de trabajo",
                fecha,
                LocalTime.of(inicio, 0),
                LocalTime.of(fin, 0),
                List.of(1)
        );
    }

    @Test
    void cancelarReservaLiberaRecurso() {
        ResultadoReserva primera = servicio.intentarReservar(
                crearDatos("111", 8, 10)
        );

        assertTrue(primera.isExitoso());

        servicio.cancelarReserva(
                primera.getReserva().getId()
        );

        ResultadoReserva segunda = servicio.intentarReservar(
                crearDatos("222", 8, 10)
        );

        assertTrue(segunda.isExitoso());
        assertEquals(2, reservaDao.listarTodos().size());
    }

    @Test
    void dosRecursosPermitenDosReservasSimultaneas() {
        recursoDao.insertar(new Recurso(
                "S2", 1, "Sala secundaria"
        ));

        ResultadoReserva primera = servicio.intentarReservar(
                crearDatos("111", 8, 10)
        );

        ResultadoReserva segunda = servicio.intentarReservar(
                crearDatos("222", 8, 10)
        );

        assertTrue(primera.isExitoso());
        assertTrue(segunda.isExitoso());

        assertNotEquals(
                primera.getReserva().getIdsRecursosAsignados(),
                segunda.getReserva().getIdsRecursosAsignados()
        );
    }

    @Test
    void rechazaReservaConHorasIguales() {
        DatosNuevaReserva datos = crearDatos(
                "111", 8, 8
        );

        assertThrows(
                ReglaDeNegocioException.class,
                () -> servicio.intentarReservar(datos)
        );
    }

    @Test
    void calendarizacionNoIncluyeReservasCanceladas() {
        ResultadoReserva creada = servicio.intentarReservar(
                crearDatos("111", 8, 10)
        );

        servicio.cancelarReserva(
                creada.getReserva().getId()
        );

        List<Reserva> resultado =
                servicio.listarReservasActivasEnFecha(fecha);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listaSoloReservasDelFuncionarioSolicitado() {
        servicio.intentarReservar(
                crearDatos("111", 8, 9)
        );

        servicio.intentarReservar(
                crearDatos("222", 9, 10)
        );

        List<Reserva> resultado =
                servicio.listarReservasDeFuncionario("111");

        assertEquals(1, resultado.size());
        assertEquals(
                "111",
                resultado.get(0).getIdFuncionario()
        );
    }
}

