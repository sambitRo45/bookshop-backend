package com.bookshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Order to which this item belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    /*
     * Book purchased.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "book_id",
            nullable = false
    )
    private Book book;

    /*
     * Number of copies purchased.
     */
    @Column(nullable = false)
    private Integer quantity;

    /*
     * Price at the time of purchase.
     *
     * We store the price here instead of always
     * reading the current Book price.
     */
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;
}