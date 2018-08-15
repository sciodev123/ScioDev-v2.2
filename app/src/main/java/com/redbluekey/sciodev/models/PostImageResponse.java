package com.redbluekey.sciodev.models;

public class PostImageResponse {

    String Message;

    public PostImageResponse(String Message) {

        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        this.Message = Message;
    }

}
