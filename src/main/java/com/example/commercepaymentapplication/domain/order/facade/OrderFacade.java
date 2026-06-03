package com.example.commercepaymentapplication.domain.order.facade;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.service.CartService;
import com.example.commercepaymentapplication.domain.order.dto.PreviewOrderResponse;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final CartService cartService;

    // 주문서 미리보기 : 재고 차감/주문 생성 없는 읽기 전용
    public PreviewOrderResponse previewOrder(Long userId, List<Long> cartItemIds){

        // cartItemsIds가 null/비어 있으면 "전체 장바구니", 값이 있으면 "선택된 아이템"만 주문서에 담음
        List<CartItem> cartItems = getValidateCartItems(
                userId,
                cartItemIds != null? cartItemIds : List.of()
        );

        // CartItem을 PreviewOrderResponse.PreviewOrderItemResponse로 변환
        List<PreviewOrderResponse.PreviewOrderItemResponse> items = cartItems.stream()
                .map(cartItem -> {
                    return new PreviewOrderResponse.PreviewOrderItemResponse(
                            cartItem.getProduct().getId(),
                            cartItem.getProduct().getName(),
                            cartItem.getProduct().getPrice(),
                            cartItem.getQuantity(),
                            cartItem.getProduct().getPrice() * cartItem.getQuantity()
                    );
                })
                .toList();

        // 장바구니 주문 총액 계산
        int totalPrice = items.stream()
                .mapToInt(PreviewOrderResponse.PreviewOrderItemResponse::subtotal)
                .sum();

        return new PreviewOrderResponse(items, totalPrice);
    }

    private List<CartItem> getValidateCartItems(Long userId, List<Long> cartItemIds) {
        // cartItemIds가 비어있으면 "전체 장바구니", 아니면 "선택된 아이템"만 조회
        List<CartItem> cartItems = cartItemIds.isEmpty()
                ? cartService.findCartEntities(userId)
                : cartService.findCartEntitiesByIds(userId, cartItemIds);

        // 1차 검증 : 요청한 ID 개수와 조회된 개수 불일치 → 일부가 "남의 것" or "존재하지 않는 ID"
        if (!cartItemIds.isEmpty() && cartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 2차 검증 : 주문한 아이템이 하나도 없는 경우
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        return cartItems;
    }
}
