package com.example.commercepaymentapplication.domain.order.dto;

import java.util.List;

public record PreviewOrderResponse(
        List<PreviewOrderItemResponse> items,
        int totalPrice
) {
    public static PreviewOrderResponse of(List<PreviewOrderItemResponse> items, int totalPrice) {
        return new PreviewOrderResponse(
                items,
                totalPrice
        );
    }
}
