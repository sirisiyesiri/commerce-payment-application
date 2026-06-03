package com.example.commercepaymentapplication.domain.user.controller;

import com.example.commercepaymentapplication.domain.auth.dto.GetUserResponse;
import com.example.commercepaymentapplication.domain.user.service.UserService;
import com.example.commercepaymentapplication.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetUserResponse>> getMyInfo(@AuthenticationPrincipal Long userId) {
        GetUserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }
}
