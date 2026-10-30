package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class IntegrationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private int statusCode;
    private String message;
    private Object body;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
