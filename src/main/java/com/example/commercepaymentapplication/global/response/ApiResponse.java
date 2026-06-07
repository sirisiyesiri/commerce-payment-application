package com.example.commercepaymentapplication.global.response;

import com.example.commercepaymentapplication.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final HttpStatus status;
    private final T data;

    private ApiResponse(HttpStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK, data);
    }
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(HttpStatus.OK, null);
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED, data);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus(), null);
    }

    // validation 전용 error 메서드
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T message) {
        return new ApiResponse<>(errorCode.getStatus(), message);
    }
}

