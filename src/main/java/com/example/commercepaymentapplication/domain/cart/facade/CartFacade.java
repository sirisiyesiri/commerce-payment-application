package com.example.commercepaymentapplication.domain.cart.facade;

import com.example.commercepaymentapplication.domain.cart.dto.AddCartRequest;
import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.domain.product.service.ProductService;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public Long addItem(Long userId, AddCartRequest request) {
        User user = userService.findUserEntity(userId);
        Product product = productService.findProductEntity(request.productId());

        CartItem cartItem = new CartItem(user, product, request.quantity());

        return cartService.addItem(cartItem);
    }
}