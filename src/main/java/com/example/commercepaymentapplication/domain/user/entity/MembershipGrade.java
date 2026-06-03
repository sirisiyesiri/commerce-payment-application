package com.example.commercepaymentapplication.domain.user.entity;

public enum MembershipGrade {
    NORMAL(0, 50_000, 1),
    VIP(50_000, 100_000, 5),
    VVIP(100_000, Integer.MAX_VALUE, 10);

    private final int minAmount;
    private final int nextAmount;
    private final int pointRatePercent;

    MembershipGrade(int minAmount, int nextAmount, int pointRatePercent) {
        this.minAmount = minAmount;
        this.nextAmount = nextAmount;
        this.pointRatePercent = pointRatePercent;
    }

    public static MembershipGrade fromTotalPaidAmount(int totalPaidAmount) {
        if (totalPaidAmount >= VVIP.minAmount) {
            return VVIP;
        }

        if (totalPaidAmount >= VIP.minAmount) {
            return VIP;
        }

        return NORMAL;
    }

    public int calculateEarnPoint(int pgPaymentAmount) {
        return pgPaymentAmount * pointRatePercent / 100;
    }

    public static int amountToNextGrade(int totalPaidAmount) {
        MembershipGrade grade = fromTotalPaidAmount(totalPaidAmount);

        if (grade == VVIP) {
            return 0;
        }

        return Math.max(0, grade.nextAmount - totalPaidAmount);
    }

    public int getPointRatePercent() {
        return pointRatePercent;
    }
}