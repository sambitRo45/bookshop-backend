package com.bookshop.dto.returnrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnBookRequestDto {

    @NotNull
    private Long orderId;

    @NotNull
    private Long bookId;

    @Min(1)
    private Integer quantity;

}