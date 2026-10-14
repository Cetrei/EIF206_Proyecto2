package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.ia.DatosReservaExtraidos;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.ArrayList;
import java.util.List;

public final class ExtractorReservaServiceProxy extends ProxyBase implements ExtractorReservaService {

    public ExtractorReservaServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles) {
        return (DatosReservaExtraidos) llamar(
                Accion.POST, Rutas.IA_EXTRAER, argumentos(frase, new ArrayList<>(categoriasDisponibles))
        );
    }

    @Override
    public boolean fueUsadoModoBasico() {
        return (Boolean) llamar(Accion.GET, Rutas.IA_MODO_BASICO, null);
    }

    @Override
    public String motivoUltimoFalloPrincipal() {
        return (String) llamar(Accion.GET, Rutas.IA_MOTIVO_FALLO, null);
    }

    @Override
    public boolean estaConfiguradoParaGemini() {
        return (Boolean) llamar(Accion.GET, Rutas.IA_CONFIGURADO_GEMINI, null);
    }
}
