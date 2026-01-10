package com.util;

public class Response<T> {
    private boolean success;
    private String message;
    private String token;  
    private T anyData;  

    public Response(boolean success, String message, String token, T anyData) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.anyData = anyData;
    }

    public Response(boolean success, String message, T anyData) {
        this(success, message, null, anyData);
    }

    public Response(boolean success, String message) {
        this(success, message, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public T getAnyData() { return anyData; }
}
