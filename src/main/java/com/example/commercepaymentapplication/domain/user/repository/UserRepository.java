package com.example.commercepaymentapplication.domain.user.repository;

import com.example.commercepaymentapplication.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cartItems WHERE u.id = :userId")
    Optional<User> findByIdWithCartItems(@Param("userId") Long userId);
}
