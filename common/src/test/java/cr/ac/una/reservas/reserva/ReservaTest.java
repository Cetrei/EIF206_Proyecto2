package cr.ac.una.reservas.reserva;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaTest {

    @Test
    void construirReservaAsignaEstadoActivaPorDefecto() {
        Reserva reserva = new Reserva(datosDeEjemplo(
                1,
                "Reunion de trabajo",
                LocalDate.of(2026, 8, 14),
                List.of(1, 2)
        ));

        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
        assertEquals(1, reserva.getId());
        assertTrue(reserva.getIdsCategoriasRequeridas().contains(1));
    }

    @Test
    void objetosCompletosTransientEmpiezanVacios() {
        Reserva reserva = new Reserva(datosDeEjemplo(
                2,
                "Charla tecnica",
                LocalDate.of(2026, 8, 20),
                List.of(2)
        ));

        assertTrue(reserva.getCategoriasRequeridas().isEmpty());
        assertTrue(reserva.getRecursosAsignados().isEmpty());
    }

    private DatosNuevaReserva datosDeEjemplo(Integer id, String actividad, LocalDate fecha, List<Integer> idsCategorias) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(id);
        datos.setIdFuncionario("111");
        datos.setActividad(actividad);
        datos.setFecha(fecha);
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(idsCategorias);
        return datos;
    }
}
