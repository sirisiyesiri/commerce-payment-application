package com.example.commercepaymentapplication.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST),  // validation 위반 요청
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),   // 서버 내부 오류

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND), // 회원을 찾을 수 없음
    DUPLICATE_EMAIL(HttpStatus.CONFLICT),   // 이미 사용 중인 이메일
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),   // 로그인 정보 불일치

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND),    // 상품을 찾을 수 없음
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT),    // 상품 재고 부족
    INVALID_PRICE(HttpStatus.BAD_REQUEST),  // 유효하지 않은 상품 가격(ex 상품 가격이 음수)
    INVALID_STOCK(HttpStatus.BAD_REQUEST),  // 유효하지 않은 상품 재고(ex 상품 재고가 음수가 되는 모든 경우)
    PRODUCT_NOT_AVAILABLE(HttpStatus.CONFLICT), // 판매중인 상품이 아님

    // Cart
    CART_EMPTY(HttpStatus.BAD_REQUEST), // 장바구니가 비어 있음
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND),  // 장바구니 상품을 찾을 수 없음
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST),   // 유효하지 않은 상품 수량(ex 수량이 0이하, 수량이 null)

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND),  // 주문을 찾을 수 없음
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST),   // 유효하지 않은 주문 상태(주문 상태 변환 기준에 위반된 경우)

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND),    // 결제 정보를 찾을 수 없음
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST),    // 외부 결제 승인 금액과 서버 내 주문/결제 금액 불일치
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST), // 유효하지 않은 결제 상태(결제 상태 변환 기준에 위반된 경우)
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST), // 유효하지 않은 결제 금액(ex 사용 포인트가 주문 금액보다 큰 경우)
    PAYMENT_NOT_PAID(HttpStatus.BAD_REQUEST),   // 결제 완료 상태가 아님
    ALREADY_PROCESSED_PAYMENT(HttpStatus.CONFLICT), // 이미 처리된 결제

    // Webhook
    INVALID_WEBHOOK_SIGNATURE(HttpStatus.UNAUTHORIZED), // 웹혹 서명 검증 실패
    WEBHOOK_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND),  // 웹혹 이벤트를 찾을 수 없음

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),  // 인증되지 않은 사용자(ex 토큰 없음, 로그인 안됨)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED), // 유효하지 않은 토큰(ex 토큰이 있는데 만료/변조/형식 오류)
    FORBIDDEN(HttpStatus.FORBIDDEN); // 로그인은 되었지만 권한이 없을 때

    private final HttpStatus status;
}
