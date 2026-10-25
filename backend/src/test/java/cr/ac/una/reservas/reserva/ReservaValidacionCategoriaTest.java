
package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.persistence.falso.ReservaDaoFalso;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaValidacionCategoriaTest {

    @Test
    void noPermiteReservarCategoriaInexistente() {

        ReservaDaoFalso reservaDao = new ReservaDaoFalso();
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();

        ReservaService servicio = new ReservaServiceImpl(
                reservaDao, recursoDao, categoriaDao
        );

        DatosNuevaReserva datos = new DatosNuevaReserva(
                null,
                "111",
                "Reunion de prueba",
                LocalDate.now().plusDays(2),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                List.of(999999)
        );


        assertThrows(
                ReglaDeNegocioException.class,
                () -> servicio.intentarReservar(datos),
                "Debe rechazar las categorias inexistentes"
        );

        assertTrue(
                reservaDao.listarTodos().isEmpty(),
                "No debe guardarse una reserva invalida"
        );

    }
}

