package cr.ac.una.reservas.protocol;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.AutorizacionException;
import cr.ac.una.reservas.excepcion.ErrorInternoException;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.excepcion.ValidacionException;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.reserva.Reserva;
import cr.ac.una.reservas.reserva.ResultadoReserva;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocoloSerializacionTest {

    @SuppressWarnings("unchecked")
    private static <T> T viajar(T objeto) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
            salida.writeObject(objeto);
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) entrada.readObject();
        }
    }

    @Test
    void peticionConservaAccionRutaYCuerpo() throws Exception {
        Funcionario funcionario = new Funcionario("111", "111", "Maria Mata", "8888-8888");
        Peticion original = new Peticion(7L, Accion.POST, "/funcionarios", funcionario);

        Peticion recibida = viajar(original);

        assertEquals(7L, recibida.getId());
        assertEquals(Accion.POST, recibida.getAccion());
        assertEquals("/funcionarios", recibida.getRuta());
        Funcionario cuerpo = assertInstanceOf(Funcionario.class, recibida.getCuerpo());
        assertEquals("Maria Mata", cuerpo.getNombre());
        assertEquals("111", cuerpo.getId());
    }

    @Test
    void respuestaExitosaConGrafoDeReservaViajaCompleta() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setDescripcion("Sala para 10 personas");
        Reserva reserva = new Reserva();
        reserva.setActividad("Reunion de equipo");
        reserva.setFecha(LocalDate.of(2026, 10, 20));
        reserva.setHoraInicio(LocalTime.of(9, 0));
        reserva.setHoraFin(LocalTime.of(10, 0));
        reserva.setFuncionario(new Funcionario("222", "222", "Juan Mora", "7777-7777"));
        reserva.setCategoriasRequeridas(List.of(categoria));

        Respuesta recibida = viajar(Respuesta.exito(3L, ResultadoReserva.exito(reserva)));

        assertTrue(recibida.isExitosa());
        assertEquals(3L, recibida.getId());
        assertNull(recibida.getError());
        ResultadoReserva resultado = assertInstanceOf(ResultadoReserva.class, recibida.getCuerpo());
        assertTrue(resultado.isExitoso());
        assertEquals("Reunion de equipo", resultado.getReserva().getActividad());
        assertEquals("Juan Mora", resultado.getReserva().getFuncionario().getNombre());
        assertEquals("Sala para 10 personas", resultado.getReserva().getCategoriasRequeridas().get(0).getDescripcion());
    }

    @Test
    void respuestaConErrorDeValidacionConservaCampoYMensaje() throws Exception {
        Respuesta recibida = viajar(
                Respuesta.fracaso(4L, new ValidacionException("telefono", "El telefono es obligatorio."))
        );

        assertFalse(recibida.isExitosa());
        ValidacionException error = assertInstanceOf(ValidacionException.class, recibida.getError());
        assertEquals("telefono", error.getCampo());
        assertEquals("El telefono es obligatorio.", error.getMessage());
        assertNull(recibida.getCuerpo());
    }

    @Test
    void cadaErrorViajaConSuClase() throws Exception {
        assertInstanceOf(
                ReglaDeNegocioException.class,
                viajar(Respuesta.fracaso(1L, new ReglaDeNegocioException("No hay disponibilidad."))).getError()
        );
        assertInstanceOf(
                AutorizacionException.class,
                viajar(Respuesta.fracaso(2L, new AutorizacionException("Sin permiso."))).getError()
        );
    }

    @Test
    void errorInternoViajaConMensajeGenericoYSinCausa() throws Exception {
        Respuesta recibida = viajar(Respuesta.fracaso(5L, new ErrorInternoException()));

        ErrorInternoException error = assertInstanceOf(ErrorInternoException.class, recibida.getError());
        assertEquals(ErrorInternoException.MENSAJE_GENERICO, error.getMessage());
        assertNull(error.getCause());
    }

    @Test
    void alertaDeReservaViajaCompletaYEsNotificacion() throws Exception {
        AlertaReserva original = new AlertaReserva(
                12,
                "Juan Mora",
                "Taller de pruebas",
                LocalDate.of(2026, 10, 21),
                LocalTime.of(13, 30),
                LocalTime.of(15, 0),
                List.of("Laptop #238719", "Sala 1 primer piso")
        );

        Object recibida = viajar((Notificacion) original);

        AlertaReserva alerta = assertInstanceOf(AlertaReserva.class, recibida);
        assertEquals(12, alerta.getIdReserva());
        assertEquals("Juan Mora", alerta.getEncargado());
        assertEquals("Taller de pruebas", alerta.getActividad());
        assertEquals(LocalDate.of(2026, 10, 21), alerta.getFecha());
        assertEquals(LocalTime.of(13, 30), alerta.getHoraInicio());
        assertEquals(LocalTime.of(15, 0), alerta.getHoraFin());
        assertEquals(List.of("Laptop #238719", "Sala 1 primer piso"), alerta.getDescripcionesRecursos());
    }
}
