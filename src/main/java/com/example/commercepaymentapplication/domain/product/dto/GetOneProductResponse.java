package com.example.commercepaymentapplication.domain.product.dto;


import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record GetOneProductResponse(

    Long id,
    String name,
    Integer price,
    Integer stockQuantity,
    String category,
    ProductStatus status,
    String description,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
){ }
