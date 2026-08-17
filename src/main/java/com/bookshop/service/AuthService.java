package com.bookshop.service;

import com.bookshop.dto.auth.LoginRequestDto;
import com.bookshop.dto.auth.LoginResponseDto;
import com.bookshop.dto.auth.MessageResponseDto;
import com.bookshop.dto.auth.RegisterRequestDto;
import com.bookshop.dto.auth.VerifyOtpRequestDto;

public interface AuthService {

    MessageResponseDto register(RegisterRequestDto request);

    MessageResponseDto verifyOtp(VerifyOtpRequestDto request);

    LoginResponseDto login(LoginRequestDto request);

}