package com.example.commercepaymentapplication.domain.user.service;

import com.example.commercepaymentapplication.domain.user.dto.GetUserResponse;
import com.example.commercepaymentapplication.domain.user.dto.GetMembershipResponse;
import com.example.commercepaymentapplication.domain.user.entity.User;
import com.example.commercepaymentapplication.domain.user.repository.UserRepository;
import com.example.commercepaymentapplication.global.error.BusinessException;
import com.example.commercepaymentapplication.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetUserResponse getMyInfo(Long userId) {
        User user = findUserEntity(userId);
        return GetUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User findUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public GetMembershipResponse getMyMembership(Long userId) {
        User user = findUserEntity(userId);
        return GetMembershipResponse.from(user);
    }
}
