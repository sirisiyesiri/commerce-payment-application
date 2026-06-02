package com.example.commercepaymentapplication.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT),
    INVALID_PRICE(HttpStatus.BAD_REQUEST),
    INVALID_STOCK(HttpStatus.BAD_REQUEST),

    // Cart
    CART_EMPTY(HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST),

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_PAID(HttpStatus.BAD_REQUEST),
    ALREADY_PROCESSED_PAYMENT(HttpStatus.CONFLICT),

    // Webhook
    INVALID_WEBHOOK_SIGNATURE(HttpStatus.UNAUTHORIZED),
    WEBHOOK_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED);

    private final HttpStatus status;
}
