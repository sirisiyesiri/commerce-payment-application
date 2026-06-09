package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;

import java.time.LocalDateTime;

public record AddOrderResponse(
        Long orderId,
        String portonePaymentId,
        int paymentPrice,
        String orderName,
        String status,
        LocalDateTime createdAt
) {
    public static AddOrderResponse of(Order order, Payment payment) {
        return new AddOrderResponse(
                order.getId(),
                payment.getPortonePaymentId(),
                payment.getPgPaymentAmount(),
                order.getOrderName(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
