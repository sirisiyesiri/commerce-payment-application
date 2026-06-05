package com.example.commercepaymentapplication.domain.product.service;

import com.example.commercepaymentapplication.domain.product.dto.GetOneProductResponse;
import com.example.commercepaymentapplication.domain.product.dto.GetProductListResponse;
import com.example.commercepaymentapplication.domain.product.dto.ProductDto;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.entity.ProductCategory;
import com.example.commercepaymentapplication.domain.product.entity.ProductStatus;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
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
            ProductCategory category,
            Integer minPrice,
            Integer maxPrice,
            ProductStatus status,
            String sort,
            int page,
            int size
    ) {
        // 페이지 번호 기본 값 1
       if (page < 1) {
       throw new BusinessException(ErrorCode.INVALID_INPUT);
       }

        // 페이지 크기 기본 값 1
        if (size < 1) {
        throw new BusinessException(ErrorCode.INVALID_INPUT);
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

        List<ProductDto> productList = productPage.getContent().stream()
                .map(product -> new ProductDto(
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

    @Transactional(readOnly = true)
    public GetOneProductResponse findOne(Long productId) {
        Product product = findProductEntity(productId);

        return new GetOneProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getStatus(),
                product.getDescription(),
                product.getCreatedAt(),
                product.getModifiedAt()
        );
    }

    @Transactional(readOnly = true)
    public Product findProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    // 검증 메서드 분리
    private void validatePriceRange(Integer minPrice, Integer maxPrice) {
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

    // 정렬 메서드 분리
    private Sort createSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
        };
    }


}
