package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.categoria.CategoriaServiceImpl;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.persistence.falso.ReservaDaoFalso;
import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.funcionario.Funcionario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecursoControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void rechazaConstruirseSiElUsuarioNoEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        RecursoService recursoService = new RecursoServiceImpl(recursoDao, categoriaDao, new ReservaDaoFalso());
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, recursoDao);

        assertThrows(
                ReglaDeNegocioException.class,
                () -> new RecursoControl(new RecursoModel(), null, null, recursoService, categoriaService)
        );
    }

    @Test
    void noRechazaPorRolSiElUsuarioEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        RecursoService recursoService = new RecursoServiceImpl(recursoDao, categoriaDao, new ReservaDaoFalso());
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, recursoDao);

        try {
            new RecursoControl(new RecursoModel(), null, null, recursoService, categoriaService);
        } catch (ReglaDeNegocioException excepcionDeRol) {
            throw new AssertionError("No deberia rechazar por rol a un administrador.", excepcionDeRol);
        } catch (RuntimeException otraExcepcion) {
        }
    }

    @Test
    void recortaEspaciosDelIdYLaDescripcionAlCrear() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        Categoria categoria = new Categoria(1, "Sala para 10 personas");
        categoriaDao.insertar(categoria);
        RecursoService recursoService = new RecursoServiceImpl(recursoDao, categoriaDao, new ReservaDaoFalso());
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, recursoDao);

        RecursoModel modelo = new RecursoModel();
        TabRecursos vista = new TabRecursos(modelo);
        new RecursoControl(modelo, vista, null, recursoService, categoriaService);

        vista.mostrarId(" 238715 ");
        vista.mostrarCategoriaSeleccionada(categoria);
        vista.mostrarDescripcion("  Sala 1 primer piso  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        Recurso guardado = recursoDao.buscarPorId("238715").orElse(null);
        assertEquals("238715", guardado.getId());
        assertEquals("Sala 1 primer piso", guardado.getDescripcion());
    }

    @Test
    void recortaEspaciosDeLaDescripcionAlModificar() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        Categoria categoria = new Categoria(1, "Sala para 10 personas");
        categoriaDao.insertar(categoria);
        Recurso existente = new Recurso("238715", categoria.getId(), "Sala 1 primer piso");
        existente.setCategoria(categoria);
        recursoDao.insertar(existente);
        RecursoService recursoService = new RecursoServiceImpl(recursoDao, categoriaDao, new ReservaDaoFalso());
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, recursoDao);

        RecursoModel modelo = new RecursoModel();
        TabRecursos vista = new TabRecursos(modelo);
        new RecursoControl(modelo, vista, null, recursoService, categoriaService);
        modelo.setRecursoSeleccionado(existente);

        vista.mostrarDescripcion("  Sala 1 segundo piso  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        assertEquals("Sala 1 segundo piso", recursoDao.buscarPorId("238715").get().getDescripcion());
    }

    private static void clickearBoton(JComponent raiz, String texto) {
        AbstractButton boton = buscarBoton(raiz, texto);
        boton.doClick();
    }

    private static AbstractButton buscarBoton(java.awt.Container contenedor, String texto) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof AbstractButton && texto.equals(((AbstractButton) componente).getText())) {
                return (AbstractButton) componente;
            }
            if (componente instanceof java.awt.Container) {
                AbstractButton encontrado = buscarBoton((java.awt.Container) componente, texto);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
