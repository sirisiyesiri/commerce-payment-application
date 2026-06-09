package com.example.commercepaymentapplication.domain.product.entity;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ON_SALE("판매중"),
    DISCONTINUED("단종"),
    OUT_OF_STOCK("품절");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public boolean isPurchasable() {
        return this == ON_SALE;
    }
}