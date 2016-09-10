package util.json.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.json.ResultCode;

import java.util.ArrayList;
import java.util.List;

public class ErrorJsonResult extends AbstractJsonResult {

    private final String errorCode;
    private List<SubError> subErrorList;

    public ErrorJsonResult(ResultCode code, String errorCode) {
        super(code);
        this.errorCode = errorCode;
        subErrorList = new ArrayList<>();
    }

    public void addSubError(String errorCode) {
        subErrorList.add(new SubError(errorCode));
    }

    public void addSubError(String[] errorCodes, String field) {
        subErrorList.add(new SubError(errorCodes, field));
    }

    private class SubError {

        private final String errorCode;
        private final String[] errorCodes;
        private final String field;

        public SubError(String errorCode) {
            this.errorCode = errorCode;
            this.field = null;
            this.errorCodes = null;
        }

        public SubError(String[] errorCodes, String field) {
            this.errorCode = null;
            this.field = field;
            this.errorCodes = errorCodes;
        }

        public ObjectNode toJson() {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode errorData = mapper.createObjectNode();
            if(field != null) {
                errorData.put("field", field);
                ArrayNode subErrorCodeArray = mapper.createArrayNode();
                for(String errorCode: errorCodes) {
                    subErrorCodeArray.add(errorCode);
                }
                errorData.set("errors", subErrorCodeArray);
            } else {
                errorData.put("error", errorCode);
            }

            return errorData;
        }
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode errorObject = mapper.createObjectNode();
        errorObject.put("error", errorCode);
        // Create sub-error list
        if(subErrorList.size() > 0) {
            ArrayNode subErrorDataArray = mapper.createArrayNode();
            for (SubError subError : subErrorList) {
                subErrorDataArray.add(subError.toJson());
            }
            errorObject.set("errors", subErrorDataArray);
        }
        // Create root
        ObjectNode resultObject = super.toJson();
        resultObject.set("errors", errorObject);

        return resultObject;
    }
}
