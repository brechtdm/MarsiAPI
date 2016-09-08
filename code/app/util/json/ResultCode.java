package util.json;

/**
 * See: https://tools.ietf.org/html/rfc2616
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
