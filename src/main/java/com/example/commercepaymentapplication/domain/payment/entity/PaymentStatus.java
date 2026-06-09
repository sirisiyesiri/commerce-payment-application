package com.example.commercepaymentapplication.domain.payment.entity;

/**
 * - PENDING → COMPLETED : 결제 대기 → 결제 완료
 * - PENDING → FAILED : 결제 대기 → 결제 실패
 * - COMPLETED → REFUND : 결제 완료 → 환불
 *
 * 이 이외의 상태 변화 불가
 */

public enum PaymentStatus {

    PENDING {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == COMPLETED || target == FAILED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == REFUND;
        }
    },
    FAILED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return false;
        }
    },
    REFUND {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(PaymentStatus target);
}
