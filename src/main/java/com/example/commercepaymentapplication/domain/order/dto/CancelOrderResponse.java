package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.order.entity.OrderStatus;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record CancelOrderResponse (
   Long orderId,
   String orderNumber,
   OrderStatus orderStatus,
   PaymentStatus paymentStatus,
   LocalDateTime canceledAt
) {
    public static CancelOrderResponse of(Order order, Payment payment) {
        return new CancelOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                payment.getStatus(),
                order.getCanceledAt()
        );
    }
}
