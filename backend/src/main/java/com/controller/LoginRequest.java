package com.controller;

public class LoginRequest {
    private String emailOrUsername; // email Or Username
    private String password;

    // getters and setters
    public String getEmailOrUsername() { return emailOrUsername; }
    
    public void setEmailOrUsername(String emailOrUsername) { this.emailOrUsername = emailOrUsername; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
