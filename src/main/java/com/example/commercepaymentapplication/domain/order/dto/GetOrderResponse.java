package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponse(
        Long orderId,
        int totalPrice,
        String status,
        String orderName,
        LocalDateTime createdAt,
        List<OrderItemResponse> orderItems
) {
    public static GetOrderResponse from(Order order) {
        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new GetOrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getCreatedAt(),
                orderItems
        );
    }
}

