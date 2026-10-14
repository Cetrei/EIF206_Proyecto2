package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.categoria.Categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecursoTest {

    @Test
    void setCategoriaActualizaTambienElIdCategoriaParaMantenerlosConsistentes() {
        Recurso recurso = new Recurso("34343", 1, "Sala 1 primer piso");
        Categoria nuevaCategoria = new Categoria(2, "Proyectores");

        recurso.setCategoria(nuevaCategoria);

        assertEquals(2, recurso.getIdCategoria());
        assertEquals(nuevaCategoria, recurso.getCategoria());
    }

    @Test
    void setCategoriaConNuloNoModificaElIdCategoriaExistente() {
        Recurso recurso = new Recurso("34343", 1, "Sala 1 primer piso");

        recurso.setCategoria(null);

        assertEquals(1, recurso.getIdCategoria());
        assertNull(recurso.getCategoria());
    }

    @Test
    void categoriaEmpiezaNulaHastaQueServiceLaResuelve() {
        Recurso recurso = new Recurso("34343", 1, "Sala 1 primer piso");

        assertNull(recurso.getCategoria());
    }
}
