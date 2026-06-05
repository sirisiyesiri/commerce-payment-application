package com.example.commercepaymentapplication.domain.order.entity;

import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false,  unique = true, length = 50)
    private String orderNumber;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int totalPrice;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int usedPointAmount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PAYMENT_PENDING;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(User user,int totalPrice, int usedPointAmount, List<OrderItem> orderItems) {
        validateOrderAmount(totalPrice, usedPointAmount);

        this.user = user;
        this.orderNumber = generateOrderNumber();
        this.totalPrice = totalPrice;
        this.usedPointAmount = usedPointAmount;
        orderItems.forEach(this::addOrderItem);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public String getOrderName() {
        if (orderItems.isEmpty()) return "주문";
        String firstName = orderItems.get(0).getProductName();
        if (orderItems.size() == 1) return firstName;
        return firstName + " 외 " + (orderItems.size() - 1) + "건";
    }

    public void markAsCompleted() {
        changeStatus(OrderStatus.ORDER_COMPLETED);
    }

    public void markAsCancelled() {
        changeStatus(OrderStatus.ORDER_CANCELED);
        this.canceledAt = LocalDateTime.now();
    }

    private static String generateOrderNumber() {
        return "order_" + UUID.randomUUID();
    }

    // 주문 상태 변경 로직
    private void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitTo(newStatus)) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }
        this.status = newStatus;
    }

    private void validateOrderAmount(int totalPrice, int usedPointAmount) {
        if (totalPrice <= 0 || usedPointAmount < 0) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        if (usedPointAmount > totalPrice) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }
}

