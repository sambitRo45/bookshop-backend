package com.bookshop.service.impl;

import com.bookshop.dto.auth.LoginRequestDto;
import com.bookshop.dto.auth.LoginResponseDto;
import com.bookshop.dto.auth.MessageResponseDto;
import com.bookshop.dto.auth.RegisterRequestDto;
import com.bookshop.dto.auth.VerifyOtpRequestDto;
import com.bookshop.entity.PendingUser;
import com.bookshop.entity.User;
import com.bookshop.repository.PendingUserRepository;
import com.bookshop.repository.UserRepository;
import com.bookshop.security.JwtService;
import com.bookshop.service.AuthService;
import com.bookshop.service.EmailService;
import com.bookshop.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final OtpUtil otpUtil;
    private final JwtService jwtService;

    @Value("${otp.expiry.minutes}")
    private int otpExpiryMinutes;

    @Override
    public MessageResponseDto register(RegisterRequestDto request) {

        // Password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and Confirm Password do not match.");
        }

        // Check verified user
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered.");
        }

        // Remove previous pending registration
        pendingUserRepository.deleteByEmail(request.getEmail());

        // Generate OTP
        String otp = otpUtil.generateOtp();

        // Encrypt password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        PendingUser pendingUser = PendingUser.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(request.getRole())
                .otp(passwordEncoder.encode(otp))
                .otpExpiryTime(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .build();

        pendingUserRepository.save(pendingUser);

        emailService.sendOtpEmail(
                request.getEmail(),
                request.getFullName(),
                otp
        );

        return new MessageResponseDto(
                "OTP sent successfully to your email."
        );
    }

    @Override
    public MessageResponseDto verifyOtp(VerifyOtpRequestDto request) {

        PendingUser pendingUser = pendingUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Registration request not found."));

        // Check OTP expiry
        if (pendingUser.getOtpExpiryTime().isBefore(LocalDateTime.now())) {

            pendingUserRepository.delete(pendingUser);

            throw new RuntimeException("OTP has expired.");
        }

        // Verify OTP
        if (!passwordEncoder.matches(request.getOtp(), pendingUser.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Create verified user
        User user = User.builder()
                .fullName(pendingUser.getFullName())
                .email(pendingUser.getEmail())
                .password(pendingUser.getPassword())
                .role(pendingUser.getRole())
                .build();

        userRepository.save(user);

        // Remove temporary registration
        pendingUserRepository.delete(pendingUser);

        return new MessageResponseDto(
                "Email verified successfully. You can now login."
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        String token = jwtService.generateToken(user);

        return new LoginResponseDto(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}