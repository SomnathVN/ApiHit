package com.example.apihit.domain.model;

public class StudentDataModel {
    // Model for login request
    public static class LoginRequest {
        private String username;
        private String password;
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    // Model for login response
    public static class LoginResponse {
        private String token;
        public String getToken() { return token; }
    }
} 