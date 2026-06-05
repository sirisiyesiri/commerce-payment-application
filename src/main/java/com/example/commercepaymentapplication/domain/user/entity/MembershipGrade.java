package com.example.commercepaymentapplication.domain.user.entity;

import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;

public enum MembershipGrade {
    NORMAL(0, 50_000, 1),
    VIP(50_000, 100_000, 5),
    VVIP(100_000, Integer.MAX_VALUE, 10);

    private final int minAmount;
    private final int nextGradeAmount;
    private final int pointRatePercent;

    MembershipGrade(int minAmount, int nextGradeAmount, int pointRatePercent) {
        this.minAmount = minAmount;
        this.nextGradeAmount = nextGradeAmount;
        this.pointRatePercent = pointRatePercent;
    }

    // 누적 결제 금액 기준으로 멤버십 등급을 반환한다.
    public static MembershipGrade fromTotalPaidAmount(int totalPaidAmount) {
        if (totalPaidAmount >= VVIP.minAmount) {
            return VVIP;
        }

        if (totalPaidAmount >= VIP.minAmount) {
            return VIP;
        }

        return NORMAL;
    }

    // PG 실결제 금액 기준으로 적립 포인트를 계산한다.
    public int calculateEarnedPoint(int pgPaymentAmount) {
        validatePgPaymentAmount(pgPaymentAmount);
        return pgPaymentAmount * pointRatePercent / 100;
    }

    // 다음 등급까지 남은 결제 금액을 계산한다.
    public static int amountToNextGrade(int totalPaidAmount) {
        MembershipGrade grade = fromTotalPaidAmount(totalPaidAmount);

        if (grade == VVIP) {
            return 0;
        }

        return Math.max(0, grade.nextGradeAmount - totalPaidAmount);
    }

    // 유효하지 않은 PG 실결제 금액이면 예외를 던진다.
    private static void validatePgPaymentAmount(int pgPaymentAmount) {
        if (pgPaymentAmount < 0) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }

    // 현재 등급의 포인트 적립률을 반환한다.
    public int getPointRatePercent() {
        return pointRatePercent;
    }
}