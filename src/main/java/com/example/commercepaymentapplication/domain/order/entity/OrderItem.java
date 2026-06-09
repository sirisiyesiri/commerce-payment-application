package com.example.commercepaymentapplication.domain.order.entity;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 50)
    private String productName;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int orderPrice;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int quantity;

    @Column(name = "cart_item_id", nullable = false)
    private Long cartItemId;

    public OrderItem(Product product, int orderPrice, int quantity, Long cartItemId) {
        this.product = product;
        this.productName = product.getName();   // 스냅샷
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.cartItemId = cartItemId;
    }

    public static OrderItem of(CartItem cartItem, Product product) {

        return new OrderItem(
                product,
                product.getPrice(),
                cartItem.getQuantity(),
                cartItem.getId()
        );
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public int getSubtotal() {
        return orderPrice * quantity;
    }
}
