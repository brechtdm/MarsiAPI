package util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import util.ConfigurationHelper;

public class ErrorJsonResult implements JsonResult {

    private final String apiVersion;
    private final int code;
    private final String message;
    private String extendedHelper;
    private String sendReport;
    private List<String> subErrorList;

    public ErrorJsonResult(int code, String message) {
        this.code = code;
        this.message = message;
        subErrorList = new ArrayList<>();
        // API version is defined in application.conf
        apiVersion = ConfigurationHelper.getConfigurationString("app.version");
    }

    public void setExtendedHelper(String extendedHelper) {
        this.extendedHelper = extendedHelper;
    }

    public void setSendReport(String sendReport) {
        this.sendReport = sendReport;
    }

    public void addSubError(String subError) {
        subErrorList.add(subError);
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode errorData = mapper.createObjectNode();
        errorData.put("message", message);
        // Add extendedHelper only if it is filled in
        if(extendedHelper != null && !extendedHelper.isEmpty()) {
            errorData.put("extendedHelper", extendedHelper);
        }
        // Add sendReport only if it is filled in
        if(sendReport != null && !sendReport.isEmpty()) {
            errorData.put("sendReport", sendReport);
        }
        // Create sub-error list
        if(subErrorList.size() > 0) {
            ArrayNode subErrorData = mapper.createArrayNode();
            for (String subError : subErrorList) {
                subErrorData.add(
                        mapper.createObjectNode().put("message", subError)
                );
            }
            errorData.set("errors", subErrorData);
        }
        // Create root
        ObjectNode errorObject = mapper.createObjectNode();
        errorObject.put("apiVersion", apiVersion);
        errorObject.put("code", code);
        errorObject.set("errors", errorData);

        return errorObject;
    }
}
