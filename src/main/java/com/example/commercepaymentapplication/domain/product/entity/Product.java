package com.example.commercepaymentapplication.domain.product.entity;

import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "int UNSIGNED")
    private Integer price;

    @Column(nullable = false, columnDefinition = "int UNSIGNED DEFAULT 0")
    private Integer stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public Product(String name, Integer price, Integer stockQuantity, ProductStatus status, ProductCategory category, String description) {
        if (price < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (stockQuantity < 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.category = category;
        this.description = description;
    }

    // 재고 차감 메서드
    public void deductStock(int orderStockQuantity) {
        validatePurchasable();
        validateStock(orderStockQuantity);

        stockQuantity -= orderStockQuantity;

        changeProductStatus();
    }

    // 재고 복구 메서드
    public void restoreStock(int quantity) {
        validateQuantity(quantity);

        this.stockQuantity += quantity;

        changeProductStatus();
    }

    public void validatePurchasable() {
        if (!this.status.isPurchasable()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    public void validateStock(int quantity) {
        validateQuantity(quantity);
        validateStockQuantity(quantity);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
    }

    private void validateStockQuantity(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private void changeProductStatus() {
        if (this.status == ProductStatus.DISCONTINUED) {
            return;
        }

        if (this.stockQuantity == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else {
            this.status = ProductStatus.ON_SALE;
        }
    }
}
