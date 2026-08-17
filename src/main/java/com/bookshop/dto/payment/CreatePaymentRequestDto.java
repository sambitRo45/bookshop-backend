package com.bookshop.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {

    @NotNull
    private Long orderId;

}