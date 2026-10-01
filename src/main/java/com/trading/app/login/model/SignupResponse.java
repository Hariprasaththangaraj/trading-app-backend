package com.trading.app.login.model;

import org.antlr.v4.runtime.misc.NotNull;

public class SignupResponse {

    @NotNull
    private Long userId;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String authProvider;

    @NotNull
    private String token;

    @NotNull
    private String message;

    public SignupResponse() {
    }

    public SignupResponse(
            Long userId,
            String name,
            String email,
            String username,
            String authProvider,
            String message) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.authProvider = authProvider;
        this.message = message;
    }

    public SignupResponse(
            Long userId,
            String name,
            String email,
            String username,
            String authProvider,
            String token,
            String message) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.authProvider = authProvider;
        this.token = token;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}