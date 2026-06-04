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

    public void completePayment(int paymentAmount) {
        this.totalPaidAmount += paymentAmount;
        updateMembershipGrade();
    }

    public void refundPayment(int refundAmount) {
        this.totalPaidAmount = Math.max(0, this.totalPaidAmount - refundAmount);
        updateMembershipGrade();
    }

    public int getAmountToNextGrade() {
        return MembershipGrade.amountToNextGrade(this.totalPaidAmount);
    }

    public int getMembershipPointRatePercent() {
        return this.membershipGrade.getPointRatePercent();
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

    private void updateMembershipGrade() {
        MembershipGrade newGrade = MembershipGrade.fromTotalPaidAmount(this.totalPaidAmount);

        if (this.membershipGrade != newGrade) {
            this.membershipGrade = newGrade;
            this.gradeChangedAt = LocalDateTime.now();
        }
    }
}