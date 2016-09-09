package util.json;

/**
 * See: https://tools.ietf.org/html/rfc2616
 */
public enum ResultCode {

    OK(200),
    NOT_FOUND(404);

    private int code;

    private ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
