package com.example.commercepaymentapplication.domain.payment.dto;

import com.example.commercepaymentapplication.domain.payment.entity.Payment;

public record ConfirmPaymentResponse(
        Long paymentId,
        Long orderId,
        int amount,
        int usedPointAmount,
        String orderStatus,
        String paymentStatus
) {
    public static ConfirmPaymentResponse from(Payment payment) {
        return new ConfirmPaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPgPaymentAmount(),
                payment.getUsedPointAmount(),
                payment.getOrder().getStatus().name(),
                payment.getStatus().name()
        );
    }
}
