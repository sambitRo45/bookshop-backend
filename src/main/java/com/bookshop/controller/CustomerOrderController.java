package com.bookshop.controller;

import com.bookshop.dto.order.OrderCreateRequestDto;
import com.bookshop.dto.order.OrderResponseDto;
import com.bookshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderService orderService;


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderCreateRequestDto request
    ) {

        OrderResponseDto response =
                orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {

        List<OrderResponseDto> orders =
                orderService.getMyOrders();

        return ResponseEntity.ok(orders);
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long id
    ) {

        OrderResponseDto response =
                orderService.getOrderById(id);

        return ResponseEntity.ok(response);
    }
}