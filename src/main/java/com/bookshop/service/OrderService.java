package com.bookshop.service;

import com.bookshop.dto.order.OrderCreateRequestDto;
import com.bookshop.dto.order.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderCreateRequestDto request);

    OrderResponseDto getOrderById(Long orderId);

    List<OrderResponseDto> getMyOrders();

    List<OrderResponseDto> getAllOrders();
}