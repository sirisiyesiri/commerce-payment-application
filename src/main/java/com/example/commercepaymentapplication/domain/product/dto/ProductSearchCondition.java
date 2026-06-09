package com.example.commercepaymentapplication.domain.product.dto;

import com.example.commercepaymentapplication.domain.product.entity.ProductCategory;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record ProductSearchCondition(
        ProductCategory category,
        Integer minPrice,
        Integer maxPrice,
        ProductStatus status,
        String sort,
        int page,
        int size
) {
    public ProductSearchCondition {
        if (page < 1 || size < 1) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        if (minPrice != null && minPrice < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size, ProductSortType.toSort(sort));
    }
}