package com.example.commercepaymentapplication.domain.product.dto;

import java.util.List;

public record GetProductListResponse(
        List<ProductDto> productList,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) {}
