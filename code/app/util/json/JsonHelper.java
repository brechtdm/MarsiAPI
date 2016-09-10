package util.json;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonHelper {

    public static JsonNode fetchFormData(JsonNode node, Class clazz) throws InvalidJsonException {
        // Check if "data" node is available
        JsonNode dataNode = node.get("data");
        if(dataNode == null) {
            throw new InvalidJsonException("Invalid JSON: no valid \"data\" node");
        }
        // Check if "kind" value is available
        if(dataNode.get("kind") == null) {
            throw new InvalidJsonException("Invalid JSON: no valid \"kind\" element");
        }
        // Check if "kind" value is valid
        JsonRootName annotation = (JsonRootName) clazz.getAnnotation(JsonRootName.class);
        String kindElement = annotation.value();
        if(!dataNode.get("kind").asText().equals(kindElement)) {
            throw new InvalidJsonException("Invalid JSON: no valid \"kind\" element " +
                    "(\"" + annotation.value() + "\" does not equals" + dataNode.get("kind") + ")");
        }
        // Check if "form" node is available
        JsonNode formDataNode = dataNode.get("formData");
        if(formDataNode == null) {
            throw new InvalidJsonException("Invalid JSON: no valid \"form\" node");
        }

        return formDataNode;
    }
}
