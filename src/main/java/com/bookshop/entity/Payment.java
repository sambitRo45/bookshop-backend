package com.bookshop.entity;

import com.bookshop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * One payment belongs to exactly one order.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private Order order;

    /*
     * Razorpay order ID.
     */
    @Column(unique = true)
    private String razorpayOrderId;

    /*
     * Razorpay payment ID.
     */
    @Column(unique = true)
    private String razorpayPaymentId;

    /*
     * Amount paid.
     */
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    /*
     * Payment status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus =
            PaymentStatus.CREATED;
}