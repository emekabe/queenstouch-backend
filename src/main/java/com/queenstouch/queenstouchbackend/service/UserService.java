package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.dto.request.ChangePasswordRequest;
import com.queenstouch.queenstouchbackend.dto.request.UpdateProfileRequest;
import com.queenstouch.queenstouchbackend.dto.response.UserResponse;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.User;
import com.queenstouch.queenstouchbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(String email) {
        User user = findByEmail(email);
        return AuthService.mapToUserResponse(user);
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone());
        }
        userRepository.save(user);
        return AuthService.mapToUserResponse(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw AppException.badRequest("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }
}
