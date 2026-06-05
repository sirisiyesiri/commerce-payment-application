package com.example.commercepaymentapplication.domain.order.dto;

import jakarta.validation.constraints.Min;

import java.util.List;

public record AddOrderRequest(
        List<Long> cartItemIds,

        @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다.")
        int usedPointAmount
) {}
