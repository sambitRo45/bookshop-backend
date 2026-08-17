package com.bookshop.service;

import com.bookshop.dto.payment.CreatePaymentResponseDto;
import com.bookshop.dto.payment.PaymentResponseDto;
import com.bookshop.dto.payment.PaymentVerifyRequestDto;

public interface PaymentService {

    CreatePaymentResponseDto createRazorpayOrder(Long orderId);

    PaymentResponseDto verifyPayment(
            PaymentVerifyRequestDto request
    );
}