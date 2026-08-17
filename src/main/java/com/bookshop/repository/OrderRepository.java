package com.bookshop.repository;

import com.bookshop.entity.Order;
import com.bookshop.entity.User;
import com.bookshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders placed by a particular customer
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    // Find order using unique order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Check whether order number already exists
    boolean existsByOrderNumber(String orderNumber);

    // Find orders by status
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Count orders for a customer
    long countByUser(User user);
}