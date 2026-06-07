package com.example.commercepaymentapplication.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelPaymentRequest(
        @NotBlank(message = "취소 사유를 입력해주세요.")
        @Size(max = 255, message = "취소 사유는 255자 이내여야 합니다.")
        String reason
) {}
