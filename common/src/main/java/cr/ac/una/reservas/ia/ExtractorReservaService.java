package cr.ac.una.reservas.ia;

import cr.ac.una.reservas.categoria.Categoria;

import java.util.List;

public interface ExtractorReservaService {

    DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles);
    boolean fueUsadoModoBasico();
    String motivoUltimoFalloPrincipal();
    boolean estaConfiguradoParaGemini();
}
