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

        ObjectNode itemData = mapper.createObjectNode();
        itemData.put("kind", kind);
        // Add self if not null, otherwise add dataItems
        if(self != null) {
            itemData.set("self", self);
        } else if(dataItems != null) {
            ArrayNode dataItemsArray = mapper.createArrayNode();
            for (ObjectNode dataItem: dataItems) {
                dataItemsArray.add(dataItem);
            }
            itemData.set("items", dataItemsArray);
        }
        // Create root
        ObjectNode objectData = mapper.createObjectNode();
        objectData.put("apiVersion", apiVersion);
        objectData.put("code", code);
        objectData.set("data", objectData);

        return objectData;
    }
}
