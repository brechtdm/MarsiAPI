package util.json.results;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by brecht on 9/8/16.
 */
public interface JsonResult {
    public ObjectNode toJson();
}
