package util.json.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.json.ResultCode;

public class DataJsonResult extends AbstractJsonResult {

    private final String kind;
    private final String message;
    private final ObjectNode self;
    private final ObjectNode[] dataItems;

    public DataJsonResult(ResultCode code, String message) {
        super(code);
        this.kind = null;
        this.message = message;
        this.self = null;
        this.dataItems = null;
    }

    public DataJsonResult(ResultCode code, String kind, ObjectNode self) {
        super(code);
        this.kind = kind;
        this.message = null;
        this.self = self;
        this.dataItems = null;
    }

    public DataJsonResult(ResultCode code, String kind, ObjectNode[] dataItems) {
        super(code);
        this.kind = kind;
        this.message = null;
        this.self = null;
        this.dataItems = dataItems;
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode dataObject = mapper.createObjectNode();
        if(message != null) {
            dataObject.put("message", message);
        }
        // Add self if not null, otherwise add dataItems
        if(self != null) {
            dataObject.put("kind", kind);
            dataObject.set("self", self);
        } else if(dataItems != null) {
            dataObject.put("kind", kind);
            ArrayNode dataItemsArray = mapper.createArrayNode();
            for (ObjectNode dataItem: dataItems) {
                dataItemsArray.add(dataItem);
            }
            dataObject.set("items", dataItemsArray);
        }
        // Create root
        ObjectNode resultObject = super.toJson();
        resultObject.set("data", dataObject);

        return resultObject;
    }
}
