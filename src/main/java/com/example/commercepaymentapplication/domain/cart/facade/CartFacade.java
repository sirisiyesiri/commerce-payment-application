package com.example.commercepaymentapplication.domain.cart.facade;

import com.example.commercepaymentapplication.domain.cart.dto.AddCartRequest;
import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.repository.ProductRepository;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.repository.UserRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addItem(Long userId, AddCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem cartItem = new CartItem(user, product, request.quantity());

        CartItem savedCartItem = cartService.addItem(cartItem);

        return savedCartItem.getId();
    }
}