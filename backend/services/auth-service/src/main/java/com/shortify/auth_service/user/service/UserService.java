package com.shortify.auth_service.user.service;

import com.shortify.auth_service.common.exception.AuthException;
import com.shortify.auth_service.entity.User;
import com.shortify.auth_service.user.dto.UserProfileResponse;
import com.shortify.auth_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found: " + username, HttpStatus.NOT_FOUND));
        return UserProfileResponse.from(user);
    }
}
