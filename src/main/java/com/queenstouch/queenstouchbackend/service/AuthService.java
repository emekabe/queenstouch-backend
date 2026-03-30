package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.dto.request.*;
import com.queenstouch.queenstouchbackend.dto.response.AuthResponse;
import com.queenstouch.queenstouchbackend.dto.response.UserResponse;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.User;
import com.queenstouch.queenstouchbackend.repository.UserRepository;
import com.queenstouch.queenstouchbackend.security.JwtUtils;
import com.queenstouch.queenstouchbackend.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final long OTP_EXPIRY_MINUTES = 15;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("An account with this email already exists");
        }

        String otp = OtpUtil.generateOtp();
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .emailVerificationOtp(otp)
                .emailVerificationOtpExpiry(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60))
                .build();

        userRepository.save(user);

        // TODO: send OTP via email — log for demo
        log.info("=== EMAIL VERIFICATION OTP for {} : {} ===", user.getEmail(), otp);

        return mapToUserResponse(user);
    }

    public void verifyEmail(VerifyEmailRequest request) {
        User user = findUserByEmail(request.getEmail());

        if (user.isEmailVerified()) {
            throw AppException.badRequest("Email is already verified");
        }
        if (user.getEmailVerificationOtp() == null || !user.getEmailVerificationOtp().equals(request.getOtp())) {
            throw AppException.badRequest("Invalid OTP");
        }
        if (Instant.now().isAfter(user.getEmailVerificationOtpExpiry())) {
            throw AppException.badRequest("OTP has expired. Please request a new one");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationOtp(null);
        user.setEmailVerificationOtpExpiry(null);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = findUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw AppException.unauthorized("Invalid email or password");
        }
        if (!user.isEmailVerified()) {
            throw AppException.forbidden("Please verify your email before logging in");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtUtils.isTokenValid(token)) {
            throw AppException.unauthorized("Invalid or expired refresh token");
        }
        String email = jwtUtils.extractSubject(token);
        User user = findUserByEmail(email);
        return buildAuthResponse(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = findUserByEmail(request.getEmail());
        String otp = OtpUtil.generateOtp();
        user.setPasswordResetOtp(otp);
        user.setPasswordResetOtpExpiry(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));
        userRepository.save(user);

        // TODO: send OTP via email — log for demo
        log.info("=== PASSWORD RESET OTP for {} : {} ===", user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = findUserByEmail(request.getEmail());

        if (user.getPasswordResetOtp() == null || !user.getPasswordResetOtp().equals(request.getOtp())) {
            throw AppException.badRequest("Invalid OTP");
        }
        if (Instant.now().isAfter(user.getPasswordResetOtpExpiry())) {
            throw AppException.badRequest("OTP has expired. Please request a new one");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpiry(null);
        userRepository.save(user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> AppException.notFound("No account found with this email"));
    }

    public static UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
