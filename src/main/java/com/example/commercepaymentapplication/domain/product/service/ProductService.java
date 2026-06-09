package com.example.commercepaymentapplication.domain.product.service;

import com.example.commercepaymentapplication.domain.product.dto.GetOneProductResponse;
import com.example.commercepaymentapplication.domain.product.dto.GetProductListResponse;
import com.example.commercepaymentapplication.domain.product.dto.ProductDto;
import com.example.commercepaymentapplication.domain.product.dto.ProductSearchCondition;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public GetProductListResponse findAll(ProductSearchCondition condition) {
        Page<Product> productPage = productRepository.findProducts(
                condition.category(),
                condition.minPrice(),
                condition.maxPrice(),
                condition.status(),
                condition.toPageable()
        );

        List<ProductDto> productList = productPage.getContent().stream()
                .map(ProductDto::from)
                .toList();

        return GetProductListResponse.of(
                productList,
                condition.page(),
                condition.size(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public GetOneProductResponse findOne(Long productId) {
        Product product = findProductEntity(productId);

        return GetOneProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public Product findProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    // 락 조회 메서드
    public Product findProductEntityForUpdate(Long productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
