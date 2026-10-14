package cr.ac.una.reservas.categoria;

import cr.ac.una.reservas.persistence.falso.CategoriaDaoFalso;
import cr.ac.una.reservas.persistence.falso.RecursoDaoFalso;
import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.funcionario.Funcionario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void rechazaConstruirseSiElUsuarioNoEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));
        CategoriaService categoriaService = new CategoriaServiceImpl(new CategoriaDaoFalso(), new RecursoDaoFalso());

        assertThrows(
                ReglaDeNegocioException.class,
                () -> new CategoriaControl(new CategoriaModel(), null, null, categoriaService)
        );
    }

    @Test
    void noRechazaPorRolSiElUsuarioEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaService categoriaService = new CategoriaServiceImpl(new CategoriaDaoFalso(), new RecursoDaoFalso());

        try {
            new CategoriaControl(new CategoriaModel(), null, null, categoriaService);
        } catch (ReglaDeNegocioException excepcionDeRol) {
            throw new AssertionError("No deberia rechazar por rol a un administrador.", excepcionDeRol);
        } catch (RuntimeException otraExcepcion) {
        }
    }

    @Test
    void recortaEspaciosDeLaDescripcionAlCrear() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, new RecursoDaoFalso());

        CategoriaModel modelo = new CategoriaModel();
        TabCategorias vista = new TabCategorias(modelo);
        new CategoriaControl(modelo, vista, null, categoriaService);

        vista.mostrarDescripcion("  Sala de Juntas  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        assertEquals(1, categoriaDao.listarTodos().size());
        assertEquals("Sala de Juntas", categoriaDao.listarTodos().get(0).getDescripcion());
    }

    @Test
    void recortaEspaciosDeLaDescripcionAlModificar() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        Categoria existente = new Categoria(1, "Sala de Juntas");
        categoriaDao.insertar(existente);
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDao, new RecursoDaoFalso());

        CategoriaModel modelo = new CategoriaModel();
        TabCategorias vista = new TabCategorias(modelo);
        new CategoriaControl(modelo, vista, null, categoriaService);
        modelo.setCategoriaSeleccionada(existente);

        vista.mostrarDescripcion("  Sala de Juntas Principal  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        assertEquals("Sala de Juntas Principal", categoriaDao.buscarPorId(1).get().getDescripcion());
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
