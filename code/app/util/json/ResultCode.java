package util.json;

/**
 * Created by brecht on 9/8/16.
 */
public enum ResultCode {

    ok(200);

    private int code;

    private ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
