package util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DataJsonResult extends AbstractJsonResult {

    private final String kind;
    private ObjectNode self;
    private ObjectNode[] dataItems;

    private DataJsonResult(int code, String kind) {
        super(code);
        this.kind = kind;
    }

    public DataJsonResult(int code, String kind, ObjectNode self) {
        this(code, kind);
        this.self = self;
    }

    public DataJsonResult(int code, String kind, ObjectNode[] dataItems) {
        this(code, kind);
        this.dataItems = dataItems;
    }

    @Override
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode dataObject = mapper.createObjectNode();
        dataObject.put("kind", kind);
        // Add self if not null, otherwise add dataItems
        if(self != null) {
            dataObject.set("self", self);
        } else if(dataItems != null) {
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
