package com.bookshop.dto.payment;

import com.bookshop.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponseDto {

    private Long paymentId;

    private Long orderId;

    private String orderNumber;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private BigDecimal amount;

    private PaymentStatus paymentStatus;
}