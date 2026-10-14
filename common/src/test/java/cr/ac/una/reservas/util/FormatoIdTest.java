package cr.ac.una.reservas.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatoIdTest {

    @ParameterizedTest
    @CsvSource({"1,RES-000001", "42,RES-000042", "123456,RES-123456"})
    void formateaReservaConPrefijoYCeros(int id, String esperado) {
        assertEquals(esperado, FormatoId.reserva(id));
    }

    @ParameterizedTest
    @CsvSource({"1,CAT-000001", "7,CAT-000007"})
    void formateaCategoriaConPrefijoYCeros(int id, String esperado) {
        assertEquals(esperado, FormatoId.categoria(id));
    }

    @Test
    void idNuloSeFormateaVacio() {
        assertEquals("", FormatoId.reserva(null));
        assertEquals("", FormatoId.categoria(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RES-000012", "res-12", "  12  ", "12", "Res-0012"})
    void interpretaTextoDeReserva(String texto) {
        assertEquals(Optional.of(12), FormatoId.interpretarReserva(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAT-000003", "cat-3", "3"})
    void interpretaTextoDeCategoria(String texto) {
        assertEquals(Optional.of(3), FormatoId.interpretarCategoria(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "abc", "CAT-000001", "RES-", "RES-1x", "1234567890"})
    void rechazaTextoQueNoEsIdDeReserva(String texto) {
        assertTrue(FormatoId.interpretarReserva(texto).isEmpty());
    }

    @Test
    void textoNuloNoSeInterpreta() {
        assertTrue(FormatoId.interpretarReserva(null).isEmpty());
        assertTrue(FormatoId.interpretarCategoria(null).isEmpty());
    }
}
