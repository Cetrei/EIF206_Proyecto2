package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.reserva.ReservaService;

public interface ProveedorServicios {
    CategoriaService categoria();

    ReservaService reserva();

    RecursoService recurso();

    FuncionarioService funcionario();

    AutenticacionService autenticacion();

    EstadisticaService estadistica();

    ConfiguracionIaService configuracionIa();

    ExtractorReservaService extractorReserva();
}
