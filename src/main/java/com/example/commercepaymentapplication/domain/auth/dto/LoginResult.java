package com.example.commercepaymentapplication.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResult {

    private final String token;
    private final LoginResponse loginResponse;

    public LoginResult(String token, LoginResponse loginResponse) {
        this.token = token;
        this.loginResponse = loginResponse;
    }
}
