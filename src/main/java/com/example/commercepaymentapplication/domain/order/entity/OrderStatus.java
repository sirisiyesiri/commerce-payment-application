package com.example.commercepaymentapplication.domain.order.entity;

/**
 * - PAYMENT_PENDING → ORDER_COMPLETED : 결제 대기 → 주문 완료(결제 성공 = 주문 성공)
 * - PAYMENT_PENDING → ORDER_CANCELED : 결제 대기 → 주문 취소(결제 실패 or 주문 취소)
 * - ORDER_COMPLETED → ORDER_CANCELED : 주문 완료 → 주문 취소(전체 환불)
 *
 * 이 이외의 상태 변화 불가
 */

public enum OrderStatus {

    PAYMENT_PENDING {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return target == ORDER_COMPLETED || target == ORDER_CANCELED;
        }
    },
    ORDER_COMPLETED {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return target == ORDER_CANCELED;
        }
    },
    ORDER_CANCELED {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(OrderStatus target);
}
