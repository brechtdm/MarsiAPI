package util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class ErrorJsonResult implements JsonResult {

    private static final String ERROR_ROOT_NODE = "error";

    private int code;
    private String message;
    private String extendedHelper;
    private String sendReport;
    private List<String> subErrorList;

    public ErrorJsonResult(int code, String message) {
        this.code = code;
        this.message = message;
        subErrorList = new ArrayList<>();
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
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode errorData = mapper.createObjectNode();
        errorData.put("code", code);
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
        JsonNode errorObject = mapper.createObjectNode();
        errorData.set(ERROR_ROOT_NODE, errorData);

        return errorData;
    }
}
