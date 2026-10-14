package cr.ac.una.reservas.reserva;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatosNuevaReservaTest {

    @Test
    void elConstructorCompletoAsignaTodosLosCampos() {
        DatosNuevaReserva datos = new DatosNuevaReserva(
                1,
                "111",
                "Sesion de Junta Directiva",
                LocalDate.of(2026, 8, 5),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of(1, 2)
        );

        assertEquals(1, datos.getId());
        assertEquals("111", datos.getIdFuncionario());
        assertEquals("Sesion de Junta Directiva", datos.getActividad());
        assertEquals(LocalDate.of(2026, 8, 5), datos.getFecha());
        assertEquals(LocalTime.of(9, 0), datos.getHoraInicio());
        assertEquals(LocalTime.of(11, 0), datos.getHoraFin());
        assertEquals(2, datos.getIdsCategoriasRequeridas().size());
    }

    @Test
    void elConstructorVacioDejaLaListaDeCategoriasVaciaYElIdNulo() {
        DatosNuevaReserva datos = new DatosNuevaReserva();

        assertNull(datos.getId());
        assertTrue(datos.getIdsCategoriasRequeridas().isEmpty());
    }

    @Test
    void permiteAsignarElIdCuandoLaReservaSeVaAModificar() {
        DatosNuevaReserva datos = new DatosNuevaReserva();

        datos.setId(9);

        assertEquals(9, datos.getId());
    }
}
