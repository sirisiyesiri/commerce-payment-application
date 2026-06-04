package com.example.commercepaymentapplication.domain.cart.service;


import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    @Transactional
    public Long addItem(CartItem newCartItem) {
        Long userId = newCartItem.getUserId();
        Long productId = newCartItem.getProductId();
        Product product = newCartItem.getProduct();

        // 판매중인 상품인지 검증
        validateOnSale(product);

        // 장바구니에 이미 있는 상품인지 조회
        CartItem cartItem = cartItemRepository
                .findByUser_IdAndProduct_Id(userId, productId)
                .orElse(null);

        // 없다면 새로 담기
        if (cartItem == null) {
            // 재고 초과 검증 후
            validateStock(product, newCartItem.getQuantity());
            // 저장하고 반환하기
            return cartItemRepository.save(newCartItem).getId();
        }

        // 기존 수량과 합산
        int newQuantity = cartItem.getQuantity() + newCartItem.getQuantity();

        // 합산 수량이 재고 초과하는지 검증
        validateStock(product, newQuantity);

        // 기존 수량 증가
        cartItem.addQuantity(newCartItem.getQuantity());

        return cartItem.getId();
    }

    // 판매중인 상품인지 검증
    private void validateOnSale(Product product) {
        if (!product.getStatus().isPurchasable()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    // 요청 수량이 상품 재고 이하인지 검증
    private void validateStock(Product product, int quantity) {
        // 요청 수량이 재고보다 크면 재고 부족 예외
        if (quantity > product.getStockQuantity()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }
}
