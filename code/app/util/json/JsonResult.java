package util.json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by brecht on 9/8/16.
 */
public interface JsonResult {
    public JsonNode toJson();
}
