package cr.ac.una.reservas.ia;

import cr.ac.una.reservas.categoria.Categoria;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReglasExtractorReservaTest {

    @Test
    public void extraeDatosPrincipalesDeLaFrase() {

        ReglasExtractorReserva extractor =
                new ReglasExtractorReserva();

        List<Categoria> categorias =
                List.of(
                        new Categoria(
                                1,
                                "Sala de reuniones"
                        ),
                        new Categoria(
                                2,
                                "Proyector"
                        )
                );

        String frase =
                "Actividad: Reunion de proyecto "
                        + "el 15/09/2026 "
                        + "de 8:00 a 10:00 "
                        + "con Sala de reuniones y Proyector";

        DatosReservaExtraidos resultado =
                extractor.extraer(
                        frase,
                        categorias
                );

        assertEquals(
                "Reunion de proyecto",
                resultado.getActividad()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        15
                ),
                resultado.getFecha()
        );

        assertEquals(
                LocalTime.of(
                        8,
                        0
                ),
                resultado.getHoraInicio()
        );

        assertEquals(
                LocalTime.of(
                        10,
                        0
                ),
                resultado.getHoraFin()
        );

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .contains(1)
        );

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .contains(2)
        );
    }

    @Test
    public void retornaDatosVaciosSiLaFraseEstaVacia() {

        ReglasExtractorReserva extractor =
                new ReglasExtractorReserva();

        DatosReservaExtraidos resultado =
                extractor.extraer(
                        "",
                        List.of()
                );

        assertNull(resultado.getActividad());
        assertNull(resultado.getFecha());

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .isEmpty()
        );
    }

    @Test
    public void extraeFechaEnTextoConNombreDeMesEnEspanol() {

        ReglasExtractorReserva extractor = new ReglasExtractorReserva();

        String frase = "Sesion de junta directiva para el 16 de septiembre del 2026";

        DatosReservaExtraidos resultado = extractor.extraer(frase, List.of());

        assertEquals(LocalDate.of(2026, 9, 16), resultado.getFecha());
    }

    @Test
    public void extraeHorasSueltasConectadasPorInicioYTermina() {

        ReglasExtractorReserva extractor = new ReglasExtractorReserva();

        String frase =
                "Sesion de junta directiva sin ningun tipo de categoria requerida "
                        + "para el 16 de septiembre del 2026 "
                        + "con hora de inicio a las 8 am y termina a la 1 pm";

        DatosReservaExtraidos resultado = extractor.extraer(frase, List.of());

        assertEquals(LocalDate.of(2026, 9, 16), resultado.getFecha());
        assertEquals(LocalTime.of(8, 0), resultado.getHoraInicio());
        assertEquals(LocalTime.of(13, 0), resultado.getHoraFin());
    }
}
