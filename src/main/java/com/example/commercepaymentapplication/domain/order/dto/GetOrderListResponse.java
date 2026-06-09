package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;

import java.time.LocalDateTime;

public record GetOrderListResponse(
        Long orderId,
        int usedPointAmount,
        int totalPrice,
        String status,
        String orderName,
        LocalDateTime createdAt
) {
    public static GetOrderListResponse from(Order order) {

        int pgPaymentPrice = order.getTotalPrice() - order.getUsedPointAmount();

        return new GetOrderListResponse(
                order.getId(),
                order.getUsedPointAmount(),
                pgPaymentPrice,
                order.getStatus().name(),
                order.getOrderName(),
                order.getCreatedAt()
        );
    }
}

