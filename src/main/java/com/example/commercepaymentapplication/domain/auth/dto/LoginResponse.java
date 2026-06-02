package com.example.commercepaymentapplication.domain.auth.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;

public class LoginResponse {

    private final Long userId;
    private final String email;
    private final String name;

    public LoginResponse(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
