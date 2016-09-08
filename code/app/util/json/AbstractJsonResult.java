package util.json;

import com.fasterxml.jackson.databind.JsonNode;
import util.ConfigurationHelper;

public abstract class AbstractJsonResult {

    protected final String apiVersion;
    protected final int code;

    public AbstractJsonResult(int code) {
        this.code = code;
        // API version is defined in application.conf
        apiVersion = ConfigurationHelper.getConfigurationString("app.version");
    }

    public abstract JsonNode toJson();
}
