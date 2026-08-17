package com.bookshop.controller;

import com.bookshop.dto.order.OrderResponseDto;
import com.bookshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopkeeper/orders")
@RequiredArgsConstructor
public class ShopKeeperOrderController {

    private final OrderService orderService;


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {

        List<OrderResponseDto> orders =
                orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }
}