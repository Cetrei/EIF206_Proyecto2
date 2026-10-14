package cr.ac.una.reservas.ia;

import cr.ac.una.reservas.categoria.Categoria;

import java.util.List;

public class ExtractorReservaServiceImpl implements ExtractorReservaService {

    private final ExtractorReserva extractorPrincipal;
    private final ExtractorReserva extractorRespaldo;

    private boolean ultimaExtraccionConRespaldo;
    private String motivoUltimoFalloPrincipal;

    public ExtractorReservaServiceImpl() {
        this(
                new GeminiExtractorReserva(),
                new ReglasExtractorReserva()
        );
    }

    public ExtractorReservaServiceImpl(
            ExtractorReserva extractorPrincipal,
            ExtractorReserva extractorRespaldo) {

        this.extractorPrincipal = extractorPrincipal;
        this.extractorRespaldo = extractorRespaldo;
    }

    public DatosReservaExtraidos extraer(
            String frase,
            List<Categoria> categoriasDisponibles) {

        ultimaExtraccionConRespaldo = false;
        motivoUltimoFalloPrincipal = null;

        try {
            return extractorPrincipal.extraer(
                    frase,
                    categoriasDisponibles
            );

        } catch (RuntimeException excepcion) {

            ultimaExtraccionConRespaldo = true;
            motivoUltimoFalloPrincipal = excepcion.getMessage();

            return extractorRespaldo.extraer(
                    frase,
                    categoriasDisponibles
            );
        }
    }

    public boolean fueUsadoModoBasico() {
        return ultimaExtraccionConRespaldo;
    }

    public String motivoUltimoFalloPrincipal() {
        return motivoUltimoFalloPrincipal;
    }

    public boolean estaConfiguradoParaGemini() {
        if (extractorPrincipal instanceof GeminiExtractorReserva) {
            return ((GeminiExtractorReserva) extractorPrincipal).estaConfigurado();
        }
        return false;
    }
}