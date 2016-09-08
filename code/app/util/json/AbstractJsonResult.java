package util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.ConfigurationHelper;

public abstract class AbstractJsonResult {

    protected final String apiVersion;
    protected final int code;

    public AbstractJsonResult(int code) {
        this.code = code;
        // API version is defined in application.conf
        apiVersion = ConfigurationHelper.getConfigurationString("app.version");
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode resultObject = mapper.createObjectNode();
        resultObject.put("apiVersion", apiVersion);
        resultObject.put("code", code);

        return resultObject;
    }
}
