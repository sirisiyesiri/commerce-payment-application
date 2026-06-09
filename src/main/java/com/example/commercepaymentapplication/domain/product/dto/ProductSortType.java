package com.example.commercepaymentapplication.domain.product.dto;

import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum ProductSortType {
    LATEST("latest", Sort.by(Sort.Direction.DESC, "createdAt")),
    PRICE_ASC("priceAsc", Sort.by(Sort.Direction.ASC, "price")),
    PRICE_DESC("priceDesc", Sort.by(Sort.Direction.DESC, "price"));

    private final String value;
    private final Sort sort;

    ProductSortType(String value, Sort sort) {
        this.value = value;
        this.sort = sort;
    }

    public static Sort toSort(String value) {
        if (value == null || value.isBlank()) {
            return LATEST.sort;
        }

        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .map(type -> type.sort)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT));
    }
}
