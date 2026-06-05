package com.example.commercepaymentapplication.domain.cart.repository;

import com.example.commercepaymentapplication.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 장바구니 조회
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.user.id = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

    Optional<CartItem> findByIdAndUser_Id(Long cartItemId, Long userId);

    // 동일 상품 장바구니 확인
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id AND ci.user.id = :userId")
    int deleteByIdAndUser_Id(@Param("id") Long id, @Param("userId") Long userId);

    // 장바구니 개별 삭제
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :cartItemId AND ci.user.id = :userId")
    int deleteByIdAndUserId(
            @Param("cartItemId") Long cartItemId,
            @Param("userId") Long userId
    );

    // 장바구니 전체 비우기
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}