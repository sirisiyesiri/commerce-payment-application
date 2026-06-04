package com.example.commercepaymentapplication.domain.order.dto;

import com.example.commercepaymentapplication.domain.order.entity.OrderItem;

public record OrderItemResponse(
        String productName,
        int orderPrice,
        int quantity
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProductName(),
                orderItem.getOrderPrice(),
                orderItem.getQuantity()
        );
    }
}
