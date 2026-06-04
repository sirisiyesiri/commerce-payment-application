package com.example.commercepaymentapplication.domain.user.entity;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "point_balance", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer pointBalance = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_grade", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    private MembershipGrade membershipGrade = MembershipGrade.NORMAL;

    @Column(name = "total_paid_amount", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer totalPaidAmount = 0;

    @Column(name = "grade_changed_at")
    private LocalDateTime gradeChangedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pointBalance = 0;
        this.membershipGrade = MembershipGrade.NORMAL;
        this.totalPaidAmount = 0;
    }

    // 결제 완료 금액을 누적하고 멤버십 등급을 재계산한다.
    public void completePayment(int paymentAmount) {
        this.totalPaidAmount += paymentAmount;
        updateMembershipGrade();
    }

    // 환불 금액을 누적 결제 금액에서 차감하고 멤버십 등급을 재계산한다.
    public void refundPayment(int refundAmount) {
        this.totalPaidAmount = Math.max(0, this.totalPaidAmount - refundAmount);
        updateMembershipGrade();
    }

    // 다음 멤버십 등급까지 남은 결제 금액을 반환한다.
    public int getAmountToNextGrade() {
        return MembershipGrade.amountToNextGrade(this.totalPaidAmount);
    }

    // 현재 멤버십 등급의 포인트 적립률을 반환한다.
    public int getMembershipPointRatePercent() {
        return this.membershipGrade.getPointRatePercent();
    }

    // 포인트를 사용하고 잔액을 차감한다.
    public void usePoint(int usedPointAmount) {
        validatePointAmount(usedPointAmount);

        if (this.pointBalance < usedPointAmount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        this.pointBalance -= usedPointAmount;
    }

    // 포인트를 적립하고 잔액을 증가시킨다.
    public void earnPoint(int earnedPointAmount) {
        validatePointAmount(earnedPointAmount);
        this.pointBalance += earnedPointAmount;
    }

    // 환불 시 사용했던 포인트를 복구한다.
    public void restoreUsedPoint(int usedPointAmount) {
        validatePointAmount(usedPointAmount);
        this.pointBalance += usedPointAmount;
    }

    // 환불 시 기존 적립 포인트를 회수한다.
    public void revokeEarnedPoint(int earnedPointAmount) {
        validatePointAmount(earnedPointAmount);

        if (this.pointBalance < earnedPointAmount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        this.pointBalance -= earnedPointAmount;
    }

    public List<CartItem> getOrderCartItems(List<Long> cartItemIds) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return cartItems;
        }
        List<CartItem> selectedCartItems = cartItems.stream()
                        .filter(cartItem -> cartItemIds.contains(cartItem.getId()))
                        .toList();

        if (selectedCartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        return selectedCartItems;
    }

    // 유효하지 않은 포인트 금액이면 예외를 던진다.
    private void validatePointAmount(int pointAmount) {
        if (pointAmount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }
    }

    // 누적 결제 금액 기준으로 멤버십 등급을 갱신한다.
    private void updateMembershipGrade() {
        MembershipGrade newGrade = MembershipGrade.fromTotalPaidAmount(this.totalPaidAmount);

        if (this.membershipGrade != newGrade) {
            this.membershipGrade = newGrade;
            this.gradeChangedAt = LocalDateTime.now();
        }
    }
}