package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultadoReservaTest {

    @Test
    void exitoGuardaLaReservaYQuedaSinCategoriasNoDisponibles() {
        Reserva reserva = new Reserva(datosDeEjemplo());

        ResultadoReserva resultado = ResultadoReserva.exito(reserva);

        assertTrue(resultado.isExitoso());
        assertEquals(reserva, resultado.getReserva());
        assertTrue(resultado.getCategoriasNoDisponibles().isEmpty());
    }

    @Test
    void fracasoNoTraeReservaYExponeLasCategoriasNoDisponibles() {
        List<Categoria> categoriasNoDisponibles = List.of(new Categoria(1, "Sala de Juntas"));

        ResultadoReserva resultado = ResultadoReserva.fracaso(categoriasNoDisponibles);

        assertFalse(resultado.isExitoso());
        assertNull(resultado.getReserva());
        assertEquals(1, resultado.getCategoriasNoDisponibles().size());
        assertEquals(1, resultado.getCategoriasNoDisponibles().get(0).getId());
    }

    @Test
    void fracasoCopiaLaListaRecibidaYNoQuedaLigadaAEllaPorReferencia() {
        List<Categoria> categoriasOriginales = new ArrayList<>();
        categoriasOriginales.add(new Categoria(1, "Sala de Juntas"));

        ResultadoReserva resultado = ResultadoReserva.fracaso(categoriasOriginales);
        categoriasOriginales.add(new Categoria(2, "Proyector"));

        assertEquals(1, resultado.getCategoriasNoDisponibles().size());
    }

    @Test
    void fracasoConListaVaciaQuedaSinCategoriasNoDisponibles() {
        ResultadoReserva resultado = ResultadoReserva.fracaso(List.of());

        assertTrue(resultado.getCategoriasNoDisponibles().isEmpty());
    }

    @Test
    void listaDeCategoriasNoDisponiblesEsInmutableEnElCasoDeExito() {
        Reserva reserva = new Reserva(datosDeEjemplo());
        ResultadoReserva resultado = ResultadoReserva.exito(reserva);

        assertThrows(
                UnsupportedOperationException.class,
                () -> resultado.getCategoriasNoDisponibles().add(new Categoria(99, "Otra"))
        );
    }

    private DatosNuevaReserva datosDeEjemplo() {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(1);
        datos.setIdFuncionario("111");
        datos.setActividad("Reunion de trabajo");
        datos.setFecha(LocalDate.now().plusDays(1));
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(List.of(1));
        return datos;
    }
}
