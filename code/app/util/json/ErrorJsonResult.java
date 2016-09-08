package util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

public class ErrorJsonResult extends AbstractJsonResult {

    private final String message;
    private String extendedHelper;
    private String sendReport;
    private List<SubError> subErrorList;

    public ErrorJsonResult(int code, String message) {
        super(code);
        this.message = message;
        subErrorList = new ArrayList<>();
    }

    public void setExtendedHelper(String extendedHelper) {
        this.extendedHelper = extendedHelper;
    }

    public void setSendReport(String sendReport) {
        this.sendReport = sendReport;
    }

    public void addSubError(String error) {
        subErrorList.add(new SubError(error));
    }

    public void addSubError(String error, String field) {
        subErrorList.add(new SubError(error, field));
    }

    private class SubError {

        private final String error;
        private final String field;

        public SubError(String error) {
            this.error = error;
            this.field = "";
        }

        public SubError(String error, String field) {
            this.error = error;
            this.field = field;
        }

        public ObjectNode toJson() {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode errorData = mapper.createObjectNode();
            errorData.put("field", field);
            errorData.put("error", error);

            return errorData;
        }
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
            for (SubError subError : subErrorList) {
                subErrorData.add(subError.toJson());
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
