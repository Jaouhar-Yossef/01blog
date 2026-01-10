package com.controller;

public class LoginRequest {
    private String emailOrUsername; // email Or Username
    private String password;

    // getters and setters
    public String getIdentifier() { return emailOrUsername; }
    
    public void setIdentifier(String emailOrUsername) { this.emailOrUsername = emailOrUsername; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
