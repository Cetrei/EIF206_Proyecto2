package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.persistence.memoria.AdministradorDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.CategoriaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.ConfiguracionIaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.DatosIniciales;
import cr.ac.una.reservas.persistence.memoria.FuncionarioDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.RecursoDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.ReservaDaoEnMemoriaTemporal;

public final class DaoFactory {
    public static final String PROPIEDAD_MODO = "reservas.dao";
    public static final String MODO_MEMORIA = "memoria";

    private record Daos(
            FuncionarioDao funcionario,
            AdministradorDao administrador,
            CategoriaDao categoria,
            RecursoDao recurso,
            ReservaDao reserva,
            ConfiguracionIaDao configuracionIa
    ) {
    }

    private static final Daos DAOS = crear(System.getProperty(PROPIEDAD_MODO, MODO_MEMORIA));

    private DaoFactory() {
    }

    public static FuncionarioDao obtenerFuncionarioDao() {
        return DAOS.funcionario();
    }

    public static AdministradorDao obtenerAdministradorDao() {
        return DAOS.administrador();
    }

    public static CategoriaDao obtenerCategoriaDao() {
        return DAOS.categoria();
    }

    public static RecursoDao obtenerRecursoDao() {
        return DAOS.recurso();
    }

    public static ReservaDao obtenerReservaDao() {
        return DAOS.reserva();
    }

    public static ConfiguracionIaDao obtenerConfiguracionIaDao() {
        return DAOS.configuracionIa();
    }

    private static Daos crear(String modo) {
        if (MODO_MEMORIA.equalsIgnoreCase(modo.trim())) {
            return enMemoria();
        }
        throw new PersistenciaException(
                "Modo de persistencia no disponible: '" + modo + "'. Valores validos de " + PROPIEDAD_MODO + ": " + MODO_MEMORIA
        );
    }

    private static Daos enMemoria() {
        FuncionarioDao funcionarioDao = new FuncionarioDaoEnMemoriaTemporal();
        AdministradorDao administradorDao = new AdministradorDaoEnMemoriaTemporal();
        CategoriaDao categoriaDao = new CategoriaDaoEnMemoriaTemporal();
        RecursoDao recursoDao = new RecursoDaoEnMemoriaTemporal();
        ReservaDao reservaDao = new ReservaDaoEnMemoriaTemporal();
        DatosIniciales.sembrar(administradorDao, funcionarioDao, categoriaDao, recursoDao, reservaDao);
        return new Daos(
                funcionarioDao,
                administradorDao,
                categoriaDao,
                recursoDao,
                reservaDao,
                new ConfiguracionIaDaoEnMemoriaTemporal()
        );
    }
}
