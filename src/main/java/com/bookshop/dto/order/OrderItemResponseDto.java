package com.bookshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long id;

    private Long bookId;

    private String bookTitle;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}