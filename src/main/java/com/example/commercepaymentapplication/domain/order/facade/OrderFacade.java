package com.example.commercepaymentapplication.domain.order.facade;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.order.dto.PreviewOrderItemResponse;
import com.example.commercepaymentapplication.domain.order.dto.PreviewOrderResponse;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;

    // 주문서 미리보기 : 재고 차감/주문 생성 없는 읽기 전용
    @Transactional(readOnly = true)
    public PreviewOrderResponse previewOrder(Long userId, List<Long> cartItemIds){

        // cartItemsIds가 비어 있으면 "전체 장바구니", 값이 있으면 "선택된 아이템"만 주문서에 담음
        User user = userService.findUserEntity(userId);

        List<CartItem> orderCartItems = user.getOrderCartItems(cartItemIds);

        // CartItem을 PreviewOrderResponse.PreviewOrderItemResponse로 변환
        List<PreviewOrderItemResponse> items = orderCartItems.stream()
                .map(PreviewOrderItemResponse::from)
                .toList();

        // 장바구니 주문 총액 계산
        int totalPrice = items.stream()
                .mapToInt(PreviewOrderItemResponse::subtotal)
                .sum();

        return new PreviewOrderResponse(items, totalPrice);
    }
}
