package com.bookshop.controller;

import com.bookshop.dto.auth.LoginRequestDto;
import com.bookshop.dto.auth.LoginResponseDto;
import com.bookshop.dto.auth.MessageResponseDto;
import com.bookshop.dto.auth.RegisterRequestDto;
import com.bookshop.dto.auth.VerifyOtpRequestDto;
import com.bookshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public String test() {
        return "This is for testing Only";
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {

        MessageResponseDto response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponseDto> verifyOtp(
            @Valid @RequestBody VerifyOtpRequestDto request
    ) {

        MessageResponseDto response =
                authService.verifyOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request
    ) {

        LoginResponseDto response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}
