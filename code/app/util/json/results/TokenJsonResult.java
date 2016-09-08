package util.json.results;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by brecht on 9/8/16.
 */
public class TokenJsonResult extends AbstractJsonResult {

    private String token;

    public TokenJsonResult(int code, String token) {
        super(code);
        this.token = token;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode resultObject = super.toJson();
        resultObject.put("token", token);

        return resultObject;
    }
}
