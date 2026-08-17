package com.bookshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "isbn")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    private String imageUrl;

    private LocalDate publishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shop_keeper_id",
            nullable = false
    )
    private User shopKeeper;

    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}