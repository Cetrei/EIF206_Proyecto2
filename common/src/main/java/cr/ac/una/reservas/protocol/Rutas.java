package cr.ac.una.reservas.protocol;

public final class Rutas {
    public static final String SESION = "autenticacion/sesion";
    public static final String CLAVE = "autenticacion/clave";

    public static final String CATEGORIAS = "categorias";
    public static final String CATEGORIAS_BUSCAR = "categorias/buscar";

    public static final String FUNCIONARIOS = "funcionarios";
    public static final String FUNCIONARIOS_POR_ID = "funcionarios/porId";
    public static final String FUNCIONARIOS_BUSCAR = "funcionarios/buscar";

    public static final String RECURSOS = "recursos";
    public static final String RECURSOS_POR_CATEGORIA = "recursos/porCategoria";
    public static final String RECURSOS_BUSCAR = "recursos/buscar";
    public static final String RECURSOS_FILTRAR = "recursos/filtrar";

    public static final String RESERVAS = "reservas";
    public static final String RESERVAS_DE_FUNCIONARIO = "reservas/funcionario";
    public static final String RESERVAS_ACTIVAS = "reservas/activas";

    public static final String ESTADISTICAS_RECURSOS = "estadisticas/recursos";
    public static final String ESTADISTICAS_SEMANAS = "estadisticas/semanas";

    public static final String IA_CONFIGURACION = "ia/configuracion";
    public static final String IA_EXTRAER = "ia/extraer";
    public static final String IA_MODO_BASICO = "ia/modoBasico";
    public static final String IA_MOTIVO_FALLO = "ia/motivoFallo";
    public static final String IA_CONFIGURADO_GEMINI = "ia/configuradoGemini";

    private Rutas() {
    }
}
