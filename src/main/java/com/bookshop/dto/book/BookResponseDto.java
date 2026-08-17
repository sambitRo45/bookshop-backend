package com.bookshop.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BookResponseDto {

    private Long id;

    private String title;

    private String author;

    private String category;

    private String isbn;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String imageUrl;

    private LocalDate publishedDate;

    private Long shopKeeperId;

    private String shopKeeperName;
}