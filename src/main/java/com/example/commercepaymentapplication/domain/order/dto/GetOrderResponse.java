package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.payment.entity.Payment;
import com.example.commercepaymentapplication.domain.point.entity.PointTransactionType;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponse(
        Long orderId,
        Long paymentId,
        String portonePaymentId,
        int usedPointAmount,
        int expectedEarnPointAmount,
        int paymentAmount,
        String pointTransactionType,
        String status,
        String orderName,
        LocalDateTime createdAt,
        List<OrderItemResponse> orderItems
) {
    public static GetOrderResponse of(Order order, Payment payment) {
        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        int pgPaymentPrice = order.getTotalPrice() - order.getUsedPointAmount();
        int expectedEarnPointAmount = (order.getUser().getMembershipPointRatePercent() * pgPaymentPrice) / 100;

        PointTransactionType pointTransactionType = PointTransactionType.USED;
        if (order.getUsedPointAmount() == 0) {
            pointTransactionType = PointTransactionType.EARNED;
        }

        return new GetOrderResponse(
                order.getId(),
                payment.getId(),
                payment.getPortonePaymentId(),
                order.getUsedPointAmount(),
                expectedEarnPointAmount,
                pgPaymentPrice,
                pointTransactionType.name(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getCreatedAt(),
                orderItems
        );
    }
}

