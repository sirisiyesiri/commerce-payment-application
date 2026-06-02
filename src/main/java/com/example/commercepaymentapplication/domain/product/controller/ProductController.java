package com.example.commercepaymentapplication.domain.product.controller;

import com.example.commercepaymentapplication.domain.product.dto.GetProductListResponse;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;
import com.example.commercepaymentapplication.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<GetProductListResponse> findAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAll(category, minPrice, maxPrice, status, sort, page, size)
        );
    }
}
