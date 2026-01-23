package com.util;

public class Response<T> {
    private boolean success;
    private String message;

    private T anyData;  


    public Response(boolean success, String message, T anyData) {
        this.success = success;
        this.message = message;
        this.anyData = anyData;
    }


    public Response(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    
    public T getAnyData() { return anyData; }
}
