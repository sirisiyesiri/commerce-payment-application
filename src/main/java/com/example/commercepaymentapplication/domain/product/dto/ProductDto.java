package com.example.commercepaymentapplication.domain.product.dto;

import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.entity.ProductCategory;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        String name,
        Integer price,
        Integer stockQuantity,
        ProductCategory category,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getModifiedAt()
        );
    }
}
