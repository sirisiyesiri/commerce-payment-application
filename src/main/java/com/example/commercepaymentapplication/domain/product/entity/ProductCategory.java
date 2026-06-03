package com.example.commercepaymentapplication.domain.product.entity;

import lombok.Getter;

@Getter
public enum ProductCategory {
    CLOTHING("의류"),
    ELECTRONICS("전자기기"),
    BEAUTY("뷰티"),
    FOOD("식품");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }
}
