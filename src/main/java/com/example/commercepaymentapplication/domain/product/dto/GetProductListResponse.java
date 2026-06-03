package com.example.commercepaymentapplication.domain.product.dto;

import com.example.commercepaymentapplication.domain.product.entity.ProductCategory;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetProductListResponse(
        List<ProductDto> productList,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) {

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
    }
}
