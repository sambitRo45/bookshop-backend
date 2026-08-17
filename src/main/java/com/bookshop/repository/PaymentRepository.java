package com.bookshop.repository;

import com.bookshop.entity.Order;
import com.bookshop.entity.Payment;
import com.bookshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    // Find payment belonging to an order
    Optional<Payment> findByOrder(Order order);

    // Find payment using Razorpay order ID
    Optional<Payment> findByRazorpayOrderId(
            String razorpayOrderId
    );

    // Find payment using Razorpay payment ID
    Optional<Payment> findByRazorpayPaymentId(
            String razorpayPaymentId
    );

    // Check Razorpay order ID
    boolean existsByRazorpayOrderId(
            String razorpayOrderId
    );

    // Count payments by status
    long countByPaymentStatus(
            PaymentStatus paymentStatus
    );
}