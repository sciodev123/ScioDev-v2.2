package com.redbluekey.sciodev.models;

import lombok.Data;

@Data
public class UserProfile {
    private int id;
    private String username;
    private String lastLogin;
    private String createdDate;
    private String image;
    private String externalIdentifier;
    private String password;
    private String email;
    private boolean acceptEmail;
}
