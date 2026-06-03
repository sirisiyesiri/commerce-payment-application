package com.example.commercepaymentapplication.domain.order.dto;

import java.util.List;

public record PreviewOrderResponse(
        List<PreviewOrderItemResponse> items,
        int totalPrice
) {
    public record PreviewOrderItemResponse(
            Long productId,
            String productName,
            int price,
            int quantity,
            int subtotal
    ) {}
}
