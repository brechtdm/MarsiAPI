package util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

public class ErrorJsonResult extends AbstractJsonResult {

    private final String message;
    private List<SubError> subErrorList;

    public ErrorJsonResult(int code, String message) {
        super(code);
        this.message = message;
        subErrorList = new ArrayList<>();
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

        ObjectNode errorObject = mapper.createObjectNode();
        errorObject.put("message", message);
        // Create sub-error list
        if(subErrorList.size() > 0) {
            ArrayNode subErrorData = mapper.createArrayNode();
            for (SubError subError : subErrorList) {
                subErrorData.add(subError.toJson());
            }
            errorObject.set("errors", subErrorData);
        }
        // Create root
        ObjectNode resultObject = super.toJson();
        resultObject.set("errors", errorObject);

        return errorObject;
    }
}
