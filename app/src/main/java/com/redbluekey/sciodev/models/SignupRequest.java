package com.redbluekey.sciodev.models;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;

    public SignupRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
