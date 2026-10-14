package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.reserva.ReservaService;

public final class ProveedorRemoto implements ProveedorServicios {
    private final CategoriaServiceProxy categoria;
    private final ReservaServiceProxy reserva;
    private final RecursoServiceProxy recurso;
    private final FuncionarioServiceProxy funcionario;
    private final AutenticacionServiceProxy autenticacion;
    private final EstadisticaServiceProxy estadistica;
    private final ConfiguracionIaServiceProxy configuracionIa;
    private final ExtractorReservaServiceProxy extractorReserva;

    public ProveedorRemoto(ConexionBackend conexion) {
        this.categoria = new CategoriaServiceProxy(conexion);
        this.reserva = new ReservaServiceProxy(conexion);
        this.recurso = new RecursoServiceProxy(conexion);
        this.funcionario = new FuncionarioServiceProxy(conexion);
        this.autenticacion = new AutenticacionServiceProxy(conexion);
        this.estadistica = new EstadisticaServiceProxy(conexion);
        this.configuracionIa = new ConfiguracionIaServiceProxy(conexion);
        this.extractorReserva = new ExtractorReservaServiceProxy(conexion);
    }

    public void cerrarSesion() {
        autenticacion.cerrarSesion();
    }

    @Override
    public CategoriaService categoria() {
        return categoria;
    }

    @Override
    public ReservaService reserva() {
        return reserva;
    }

    @Override
    public RecursoService recurso() {
        return recurso;
    }

    @Override
    public FuncionarioService funcionario() {
        return funcionario;
    }

    @Override
    public AutenticacionService autenticacion() {
        return autenticacion;
    }

    @Override
    public EstadisticaService estadistica() {
        return estadistica;
    }

    @Override
    public ConfiguracionIaService configuracionIa() {
        return configuracionIa;
    }

    @Override
    public ExtractorReservaService extractorReserva() {
        return extractorReserva;
    }
}
