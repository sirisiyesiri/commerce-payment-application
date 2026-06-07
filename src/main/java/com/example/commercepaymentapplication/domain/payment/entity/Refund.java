package com.example.commercepaymentapplication.domain.payment.entity;

import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "refunds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Refund extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refund_request_id", nullable = false, unique = true, length = 50)
    private String refundRequestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "refunded_point_amount", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int refundedPointAmount = 0;

    @Column(name = "refunded_pg_amount", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int refundedPgAmount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status = RefundStatus.COMPLETED;

    public Refund(Payment payment, String reason) {
        this.payment = payment;
        this.reason = reason;
        this.refundedPointAmount = payment.getUsedPointAmount();
        this.refundedPgAmount = payment.getPgPaymentAmount();
        this.refundRequestId = generateRefundRequestId();
    }

    private String generateRefundRequestId() {
        return "refund_" + UUID.randomUUID();
    }

    public void changeRefundStatusToFail() {
        this.status = RefundStatus.FAIL;
    }
}
