package com.example.commercepaymentapplication.domain.payment.entity;

public enum PaymentType {

    CARD_ONLY,
    CARD_POINT,
    POINT_ONLY;

    public static PaymentType checkPaymentType(int orderAmount, int usedPointAmount) {
        if (usedPointAmount == 0) {
            return PaymentType.CARD_ONLY;
        } else if (orderAmount == usedPointAmount) {
            return PaymentType.POINT_ONLY;
        }
        return PaymentType.CARD_POINT;
    }
}
