package com.bookshop.entity;

import com.bookshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "orderNumber")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String orderNumber;

    /*
     * Customer who placed this order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    /*
     * Total amount of the complete order.
     */
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;

    /*
     * Current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /*
     * One order can contain multiple books.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
     * One order has one payment.
     */
    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private Payment payment;

    /*
     * One order can have multiple return requests.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<ReturnRequest> returnRequests = new ArrayList<>();
}