package com.bookshop.dto.order;

import com.bookshop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;

    private String orderNumber;

    private Long userId;

    private String customerName;

    private String customerEmail;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private List<OrderItemResponseDto> items;
}