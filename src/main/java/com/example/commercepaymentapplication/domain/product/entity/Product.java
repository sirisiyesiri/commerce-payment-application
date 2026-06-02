package com.example.commercepaymentapplication.domain.product.entity;

import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
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

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public Product(String name, Integer price, Integer stockQuantity, ProductStatus status, String category, String description) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다");
        }
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.category = category;
        this.description = description;
    }
}
