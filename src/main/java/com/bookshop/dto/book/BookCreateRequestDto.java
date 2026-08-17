package com.bookshop.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookCreateRequestDto {

    @NotBlank(message = "Book title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0"
    )
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(
            value = 0,
            message = "Quantity cannot be negative"
    )
    private Integer quantity;

    private String imageUrl;

    private LocalDate publishedDate;
}