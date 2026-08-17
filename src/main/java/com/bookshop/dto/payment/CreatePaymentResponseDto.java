package com.bookshop.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreatePaymentResponseDto {

    private String razorpayOrderId;

    private String razorpayKeyId;

    private BigDecimal amount;

    private String currency;

    private String orderNumber;
}