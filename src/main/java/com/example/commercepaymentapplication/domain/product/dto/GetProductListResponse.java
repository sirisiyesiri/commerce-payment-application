package com.example.commercepaymentapplication.domain.product.dto;

import java.util.List;

public record GetProductListResponse(
        List<ProductDto> productList,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) {
    public static  GetProductListResponse of(
            List<ProductDto> productList,
            int pageNumber,
            int pageSize,
            Long totalElements,
            int totalPages
    ) {
        return new GetProductListResponse(
                productList,
                pageNumber,
                pageSize,
                totalElements,
                totalPages
        );
    }
}
