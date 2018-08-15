package com.redbluekey.sciodev.models;

import lombok.Data;

@Data
public class LoginSignupResponse {
    private boolean success;
    private String message;
}
