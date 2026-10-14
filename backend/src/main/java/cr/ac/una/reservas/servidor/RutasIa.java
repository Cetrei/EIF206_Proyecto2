package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.io.Serializable;
import java.util.List;

final class RutasIa {
    private RutasIa() {
    }

    @SuppressWarnings("unchecked")
    static void registrar(Despachador despachador, ConfiguracionIaService configuracion) {
        despachador.registrar(Accion.GET, Rutas.IA_CONFIGURACION, (cuerpo, sesion) ->
                configuracion.obtenerConfiguracion().orElse(null));
        despachador.registrar(Accion.PUT, Rutas.IA_CONFIGURACION, (cuerpo, sesion) -> {
            Serializable[] argumentos = (Serializable[]) cuerpo;
            configuracion.guardar((String) argumentos[0], (String) argumentos[1]);
            return null;
        });
        despachador.registrar(Accion.POST, Rutas.IA_EXTRAER, (cuerpo, sesion) -> {
            Serializable[] argumentos = (Serializable[]) cuerpo;
            return sesion.extractor().extraer((String) argumentos[0], (List<Categoria>) argumentos[1]);
        });
        despachador.registrar(Accion.GET, Rutas.IA_MODO_BASICO, (cuerpo, sesion) ->
                sesion.extractor().fueUsadoModoBasico());
        despachador.registrar(Accion.GET, Rutas.IA_MOTIVO_FALLO, (cuerpo, sesion) ->
                sesion.extractor().motivoUltimoFalloPrincipal());
        despachador.registrar(Accion.GET, Rutas.IA_CONFIGURADO_GEMINI, (cuerpo, sesion) ->
                sesion.extractor().estaConfiguradoParaGemini());
    }
}
