package com.example.commercepaymentapplication.domain.product.service;

import com.example.commercepaymentapplication.domain.product.dto.GetProductListResponse;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public GetProductListResponse findAll(
            String category,
            Integer minPrice,
            Integer maxPrice,
            ProductStatus status,
            String sort,
            int page,
            int size
    ) {
        // 페이지 번호 기본 값 1
        if (page < 1) {
            page = 1;
        }

        // 페이지 크기 기본 값 1
        if (size < 1) {
            size = 20;
        }

        // 최소/최대 가격 검증 => 0원이거나, 최대가 최소보다 값이 적을 경우 예외처리
        validatePriceRange(minPrice, maxPrice);

        Pageable pageable = PageRequest.of(page - 1, size, createSort(sort));

        Page<Product> productPage = productRepository.findProducts(
                category,
                minPrice,
                maxPrice,
                status,
                pageable
        );

        List<GetProductListResponse.ProductDto> productList = productPage.getContent().stream()
                .map(product -> new GetProductListResponse.ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCategory(),
                        product.getStatus(),
                        product.getCreatedAt(),
                        product.getModifiedAt()
                ))
                .toList();

        return new GetProductListResponse(
                productList,
                page,
                size,
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    // 검증 메서드 분리
    private void validatePriceRange(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException("최소 가격은 0 이상이어야 합니다.");
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException("최대 가격은 0 이상이어야 합니다.");
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("최소 가격은 최대 가격보다 클 수 없습니다.");
        }
    }

    // 정렬 메서드 분리
    private Sort createSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 조건입니다.");
        };
    }


}
