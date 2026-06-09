package com.example.commercepaymentapplication.domain.payment.port;

public record PaymentGatewayResponse(
        String id,
        String status,
        int totalAmount
) {
    // 결제 게이트웨이 응답 정보를 생성한다.
    public static PaymentGatewayResponse of(String id, String status, int totalAmount) {
        return new PaymentGatewayResponse(id, status, totalAmount);
    }
}
