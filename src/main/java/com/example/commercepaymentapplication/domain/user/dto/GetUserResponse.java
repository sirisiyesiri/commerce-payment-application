package com.example.commercepaymentapplication.domain.user.dto;

import com.example.commercepaymentapplication.domain.user.entity.User;
import lombok.Getter;

@Getter
public class GetUserResponse {

    private final Long userId;
    private final String email;
    private final String name;
    private final String phoneNumber;

    public GetUserResponse(Long userId, String email, String name, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public static GetUserResponse from(User user) {
        return new GetUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber()
        );
    }
}