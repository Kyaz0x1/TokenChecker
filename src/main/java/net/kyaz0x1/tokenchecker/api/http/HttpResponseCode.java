package net.kyaz0x1.tokenchecker.api.http;

public enum HttpResponseCode {

    OK(200),
    UNAUTHORIZED(401);

    private int code;

    HttpResponseCode(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}