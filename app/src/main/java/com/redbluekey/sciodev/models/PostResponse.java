package com.redbluekey.sciodev.models;

public class PostResponse {

    String success;
    String message;

    public PostResponse(String status, String message) {

        this.success = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
}
