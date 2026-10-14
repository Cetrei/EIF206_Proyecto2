package cr.ac.una.reservas.categoria;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.recurso.Recurso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaServiceTest {

    private CategoriaDaoFalso categoriaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaService categoriaService;

    @BeforeEach
    void prepararService() {
        categoriaDao = new CategoriaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaService = new CategoriaServiceImpl(categoriaDao, recursoDao);
    }

    @Test
    void creaCategoriaConIdAutogenerado() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");

        assertEquals(1, categoria.getId());
        assertEquals("Sala de Juntas", categoria.getDescripcion());
    }

    @Test
    void generaConsecutivosDiferentesParaCategoriasSucesivas() {
        Categoria primera = categoriaService.crear("Sala de Juntas");
        Categoria segunda = categoriaService.crear("Laptop windows 11");

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
    }

    @Test
    void rechazaCrearConDescripcionVacia() {
        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.crear(" "));
    }

    @Test
    void rechazaCrearConDescripcionNula() {
        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.crear(null));
    }

    @Test
    void modificaCategoriaExistente() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        categoria.setDescripcion("Sala de Juntas Principal");

        categoriaService.modificar(categoria);

        assertEquals("Sala de Juntas Principal", categoriaDao.buscarPorId(categoria.getId()).get().getDescripcion());
    }

    @Test
    void rechazaModificarCategoriaInexistente() {
        Categoria categoria = new Categoria(999999, "No existe");

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.modificar(categoria));
    }

    @Test
    void eliminaCategoriaSinRecursosAsociados() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");

        categoriaService.eliminar(categoria.getId());

        assertTrue(categoriaDao.buscarPorId(categoria.getId()).isEmpty());
    }

    @Test
    void rechazaEliminarCategoriaConRecursosAsociados() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        recursoDao.insertar(new Recurso("238715", categoria.getId(), "Sala 1 primer piso"));

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.eliminar(categoria.getId()));
    }

    @Test
    void rechazaEliminarCategoriaInexistente() {
        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.eliminar(999999));
    }

    @Test
    void rechazaModificarConDescripcionVacia() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        categoria.setDescripcion(" ");

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.modificar(categoria));
    }

    @Test
    void noReutilizaElIdDeUnaCategoriaBorrada() {
        Categoria primera = categoriaService.crear("Sala de Juntas");
        categoriaService.crear("Laptop windows 11");
        categoriaService.eliminar(primera.getId());

        Categoria tercera = categoriaService.crear("Proyector HDMI");

        assertEquals(3, tercera.getId());
    }

    @Test
    void buscaCategoriasPorDescripcion() {
        categoriaDao.insertar(new Categoria(1, "Sala de Juntas"));
        categoriaDao.insertar(new Categoria(2, "Laptop windows 11"));

        List<Categoria> resultado = categoriaService.buscarPorDescripcion("Sala");

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getId());
    }

    @Test
    void listaTodasLasCategorias() {
        categoriaDao.insertar(new Categoria(1, "Sala de Juntas"));
        categoriaDao.insertar(new Categoria(2, "Laptop windows 11"));

        assertEquals(2, categoriaService.listarTodas().size());
    }
}
