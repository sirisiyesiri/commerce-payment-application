package com.example.commercepaymentapplication.domain.cart.entity;

import com.example.commercepaymentapplication.domain.cart.dto.AddCartRequest;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_item_user_product",
                        columnNames = {"user_id", "product_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 장바구니 소유 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 장바구니에 담긴 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 장바구니 상품 수량
    @Column(nullable = false, columnDefinition = "int UNSIGNED DEFAULT 1")
    private int quantity;

    public CartItem(User user, Product product, int quantity) {
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        validateQuantity(quantity);

        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public static CartItem from(User user, Product product, AddCartRequest request) {
        return new CartItem(
                user,
                product,
                request.quantity()
        );
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getProductId() {
        return product.getId();
    }

    // 동일 상품을 다시 담을 때 수량 합산
    public void addQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity += quantity;
    }

    // 장바구니 상품 수량 변경
    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
    }
}