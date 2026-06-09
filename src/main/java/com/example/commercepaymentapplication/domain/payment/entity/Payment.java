package com.example.commercepaymentapplication.domain.payment.entity;

import com.example.commercepaymentapplication.domain.order.entity.Order;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, unique = true, length = 100)
    private String portonePaymentId;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int orderAmount;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int usedPointAmount = 0;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int pgPaymentAmount;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int earnedPointAmount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    public Payment(Order order, int orderAmount, int usedPointAmount) {

        this.order = order;
        this.portonePaymentId = generatePortonePaymentId();
        this.orderAmount = orderAmount;
        this.usedPointAmount = usedPointAmount;
        this.pgPaymentAmount = orderAmount - usedPointAmount;
        this.type = PaymentType.checkPaymentType(orderAmount, usedPointAmount);

    }

    private static String generatePortonePaymentId() {
        return "pay_" + UUID.randomUUID();
    }

    public void markAsPaid(int earnedPointAmount) {
        changeStatus(PaymentStatus.COMPLETED);
        this.earnedPointAmount = earnedPointAmount; // 스냅샷 저장
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        changeStatus(PaymentStatus.FAILED);
    }

    public void markAsRefund() {
        changeStatus(PaymentStatus.REFUND);
    }

    // 결제 상태 변경 로직
    private void changeStatus(PaymentStatus newStatus) {
        if (!this.status.canTransitTo(newStatus)) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = newStatus;
    }
}
