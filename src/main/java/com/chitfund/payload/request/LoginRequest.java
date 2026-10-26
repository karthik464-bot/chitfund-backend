package com.chitfund.payload.request;

public class LoginRequest {
    private String username;
    private String email;
    private String password;

    public String getUsername() {
        return username != null && !username.isEmpty() ? username : email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email != null && !email.isEmpty() ? email : username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}