package util.json.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.ConfigurationHelper;
import util.json.ResultCode;

public abstract class AbstractJsonResult implements JsonResult {

    protected final String apiVersion;
    protected final ResultCode code;

    public AbstractJsonResult(ResultCode code) {
        this.code = code;
        // API version is defined in application.conf
        apiVersion = ConfigurationHelper.getConfigurationString("app.version");
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode resultObject = mapper.createObjectNode();
        resultObject.put("apiVersion", apiVersion);
        resultObject.put("code", code.getCode());

        return resultObject;
    }
}
