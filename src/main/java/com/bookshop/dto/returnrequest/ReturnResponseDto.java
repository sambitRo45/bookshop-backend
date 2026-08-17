package com.bookshop.dto.returnrequest;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReturnResponseDto {

    private Long returnId;
    private BigDecimal refundAmount;
    private String status;
    private LocalDateTime returnDate;

}