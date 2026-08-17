package com.bookshop.entity;

import com.bookshop.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Order from which the book is being returned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    /*
     * Book being returned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "book_id",
            nullable = false
    )
    private Book book;

    /*
     * Number of copies being returned.
     */
    @Column(nullable = false)
    private Integer quantity;

    /*
     * Refund amount.
     */
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal refundAmount;

    /*
     * Date and time of return request.
     */
    @Column(nullable = false)
    private LocalDateTime returnDate;

    /*
     * Current return status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReturnStatus returnStatus =
            ReturnStatus.REQUESTED;
}