package com.bookshop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequestDto {

    @NotEmpty(message = "Order must contain at least one book")
    @Valid
    private List<OrderItemRequestDto> items;
}