package com.example.commercepaymentapplication.domain.auth.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final Long userId;
    private final String email;
    private final String name;
    private final String token;

    public LoginResponse(Long userId, String email, String name, String token) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.token = token;
    }

    public static LoginResponse of(User user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                token
        );
    }
}
