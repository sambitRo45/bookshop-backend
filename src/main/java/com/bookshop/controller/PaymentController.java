package com.bookshop.controller;

import com.bookshop.dto.payment.CreatePaymentResponseDto;
import com.bookshop.dto.payment.PaymentResponseDto;
import com.bookshop.dto.payment.PaymentVerifyRequestDto;
import com.bookshop.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @PostMapping("/create/{orderId}")
    public ResponseEntity<CreatePaymentResponseDto>
    createPayment(
            @PathVariable Long orderId
    ) {

        CreatePaymentResponseDto response =
                paymentService.createRazorpayOrder(
                        orderId
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponseDto>
    verifyPayment(
            @Valid @RequestBody
            PaymentVerifyRequestDto request
    ) {

        PaymentResponseDto response =
                paymentService.verifyPayment(request);

        return ResponseEntity.ok(response);
    }
}