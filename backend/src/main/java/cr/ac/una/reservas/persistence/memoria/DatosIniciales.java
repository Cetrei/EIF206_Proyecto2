package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.recurso.Recurso;
import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.reserva.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class DatosIniciales {
    private DatosIniciales() {
    }

    public static void sembrar(
            AdministradorDao administradorDao,
            FuncionarioDao funcionarioDao,
            CategoriaDao categoriaDao,
            RecursoDao recursoDao,
            ReservaDao reservaDao
    ) {
        administradorDao.insertar(new Administrador("admin", "123"));

        funcionarioDao.insertar(new Funcionario("111", "111", "Juan Perez", "3323"));
        funcionarioDao.insertar(new Funcionario("222", "222", "Maria Perez", "222222"));
        funcionarioDao.insertar(new Funcionario("123", "123", "Joanfer Hidalgo", "0000-0000"));

        Integer sala = categoriaDao.insertar(new Categoria(null, "Sala para 10 personas")).getId();
        Integer laptop = categoriaDao.insertar(new Categoria(null, "Laptop windows")).getId();
        Integer juntas = categoriaDao.insertar(new Categoria(null, "Sala de Juntas")).getId();

        recursoDao.insertar(new Recurso("238715", laptop, "Laptop #238715"));
        recursoDao.insertar(new Recurso("34343", sala, "Sala 1 primer piso"));
        recursoDao.insertar(new Recurso("45238", laptop, "Laptop #45238"));
        recursoDao.insertar(new Recurso("452784", juntas, "Sala de Juntas Principal"));

        LocalDate hoy = LocalDate.now();
        reservaDao.insertar(reserva("111", "Reunion clientes", hoy.plusDays(1), 8, 10,
                List.of(laptop, sala), List.of("238715", "34343"), EstadoReserva.ACTIVA));
        reservaDao.insertar(reserva("222", "Charla tecnica", hoy.plusDays(6), 8, 10,
                List.of(laptop), List.of("45238"), EstadoReserva.ACTIVA));
        reservaDao.insertar(reserva("111", "Sesion de Junta Directiva", hoy.plusDays(6), 9, 11,
                List.of(laptop, juntas), List.of("238715", "452784"), EstadoReserva.ACTIVA));
        reservaDao.insertar(reserva("111", "Entrevista candidato", hoy.plusDays(7), 10, 11,
                List.of(sala), List.of("34343"), EstadoReserva.ACTIVA));
        reservaDao.insertar(reserva("111", "Reunion de Control", hoy.plusDays(13), 8, 9,
                List.of(juntas), List.of("452784"), EstadoReserva.ACTIVA));
        reservaDao.insertar(reserva("123", "Trabajo", hoy.plusDays(2), 7, 12,
                List.of(laptop), List.of("238715"), EstadoReserva.CANCELADA));
    }

    private static Reserva reserva(
            String idFuncionario,
            String actividad,
            LocalDate fecha,
            int horaInicio,
            int horaFin,
            List<Integer> idsCategorias,
            List<String> idsRecursos,
            EstadoReserva estado
    ) {
        Reserva reserva = new Reserva();
        reserva.setIdFuncionario(idFuncionario);
        reserva.setActividad(actividad);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(LocalTime.of(horaInicio, 0));
        reserva.setHoraFin(LocalTime.of(horaFin, 0));
        reserva.setIdsCategoriasRequeridas(idsCategorias);
        reserva.setIdsRecursosAsignados(idsRecursos);
        reserva.setEstado(estado);
        return reserva;
    }
}
