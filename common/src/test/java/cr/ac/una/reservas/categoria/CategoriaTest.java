package cr.ac.una.reservas.categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CategoriaTest {

    @Test
    void dosCategoriasConElMismoIdSonIgualesAunqueLaDescripcionDifiera() {
        Categoria primera = new Categoria(1, "Sala de Juntas");
        Categoria segunda = new Categoria(1, "Otra descripcion");

        assertEquals(primera, segunda);
        assertEquals(primera.hashCode(), segunda.hashCode());
    }

    @Test
    void dosCategoriasConIdDiferenteNoSonIguales() {
        Categoria primera = new Categoria(1, "Sala de Juntas");
        Categoria segunda = new Categoria(2, "Sala de Juntas");

        assertNotEquals(primera, segunda);
    }

    @Test
    void noEsIgualAUnObjetoDeOtraClase() {
        Categoria categoria = new Categoria(1, "Sala de Juntas");

        assertNotEquals(categoria, 1);
    }

    @Test
    void esIgualASiMisma() {
        Categoria categoria = new Categoria(1, "Sala de Juntas");

        assertEquals(categoria, categoria);
    }
}
