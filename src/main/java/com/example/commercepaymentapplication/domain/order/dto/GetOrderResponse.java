package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.domain.point.entity.PointTransactionType;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponse(
        Long orderId,
        String portonePaymentId,
        int totalPrice,
        int usedPointAmount,
        int paymentAmount,
        String pointTransactionType,
        String status,
        LocalDateTime createdAt,
        List<OrderItemResponse> orderItems
) {
    public static GetOrderResponse from(Order order, String portonePaymentId) {
        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        int pgPaymentPrice = order.getTotalPrice() - order.getUsedPointAmount();

        PointTransactionType pointTransactionType = PointTransactionType.USED;
        if (order.getUsedPointAmount() == 0) {
            pointTransactionType = PointTransactionType.EARNED;
        }

        return new GetOrderResponse(
                order.getId(),
                portonePaymentId,
                order.getTotalPrice(),
                order.getUsedPointAmount(),
                pgPaymentPrice,
                pointTransactionType.name(),
                order.getStatus().name(),
                order.getCreatedAt(),
                orderItems
        );
    }
}

