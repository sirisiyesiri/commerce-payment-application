package com.example.commercepaymentapplication.domain.cart.service;


import com.example.commercepaymentapplication.domain.cart.dto.CartItemDto;
import com.example.commercepaymentapplication.domain.cart.dto.GetCartItemListResponse;
import com.example.commercepaymentapplication.domain.cart.dto.UpdateCartItemQuantityRequest;
import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import com.example.commercepaymentapplication.domain.cart.repository.CartItemRepository;
import com.example.commercepaymentapplication.domain.product.entity.Product;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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

    @Transactional(readOnly = true)
    public GetCartItemListResponse getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        // cartItem 엔티티 목록을 응답 DTO 목록으로 변환
        List<CartItemDto> cartItemList = cartItems.stream()
                .map(this::toResponse)
                .toList();

        // 장바구니 전체 금액 계산 - stream으로 totalPrice만 꺼내서 더하기
        Long totalAmount = cartItemList.stream()
                .mapToLong(CartItemDto::totalPrice)
                .sum();

        // 장바구니 생성일시 - 처음 담긴 상품

        return new GetCartItemListResponse(cartItemList, totalAmount);
    }

    @Transactional
    public CartItemDto updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request) {

        // 장바구니 상품 조회 후 없으면 예외
        CartItem cartItem = cartItemRepository.findByIdAndUser_Id(cartItemId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 장바구니 상품에 연결된 상품 엔티티 조회
        Product product = cartItem.getProduct();

        // 판매중인지, 변경수량이 재고 이하인지 검증
        validateOnSale(product);
        validateStock(product, request.quantity());

        // 장바구니 상품 수량을 요청 수량으로 변경 후
        cartItem.changeQuantity(request.quantity());

        // DTO로 반환
        return toResponse(cartItem);
    }

    @Transactional
    public void removeOneItem(Long userId, Long cartItemId) {
        // 사용자 ID와 장바구니 상품 ID가 일치하는 장바구니 상품을 DB에서 삭제
        int deleted = cartItemRepository.deleteByIdAndUserId(cartItemId,userId);
        if(deleted != 1) {
            log.warn("장바구니 상품 삭제 실패: 유효하지 않은 상품 ID입니다. : expected=1, actual={}, userId={}, cartItemId={}"
                    , deleted, userId, cartItemId);
        }
    }

    @Transactional
    public void removeAllItems(Long userId) {
        // 사용자 ID 확인해서 장바구니 전체 삭제
        int deleted = cartItemRepository.deleteAllByUserId(userId);
        // 전체 비우기 시 멱등적으로 보기
        // 비어도 성공, 있어도 삭제 성공 -> 결과적으로 비어있으면 성공
        log.info("장바구니 전체 비우기 완료: deleted={}, userId={}", deleted, userId);

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

    // 장바구니에 담긴 수량 * 상품 가격 구하는 메서드
    private CartItemDto toResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();

        Long totalPrice = (long) product.getPrice() * cartItem.getQuantity();

        return new CartItemDto(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                totalPrice,
                cartItem.getCreatedAt(),
                cartItem.getModifiedAt()
        );
    }

    @Transactional
    public void clearCartItems(List<Long> cartItemIds, Long memberId) {
        int deleted = cartItemRepository.deleteAllByIdInAndUserId(cartItemIds, memberId);
        if(deleted != cartItemIds.size()) {
            log.warn("장바구니 삭제 불일치 : expected={}, actual={}, memberId={}", cartItemIds.size(), deleted, memberId);
        }
    }
}
