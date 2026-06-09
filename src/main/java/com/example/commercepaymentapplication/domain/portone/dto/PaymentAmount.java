package com.example.commercepaymentapplication.domain.portone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentAmount(
        int total     // 총 결제 금액
) {}
