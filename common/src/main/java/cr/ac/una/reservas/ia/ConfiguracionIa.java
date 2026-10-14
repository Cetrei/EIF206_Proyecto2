package cr.ac.una.reservas.ia;

import java.io.Serializable;

public class ConfiguracionIa implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ID_UNICO = "CONFIG-IA";

    private String id = ID_UNICO;

    private String apiKey;

    private String modelo;

    public ConfiguracionIa() {
    }

    public ConfiguracionIa(String apiKey, String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
