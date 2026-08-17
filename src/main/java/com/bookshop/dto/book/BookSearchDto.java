package com.bookshop.dto.book;

import lombok.Data;

@Data
public class BookSearchDto {

    private String title;
    private String author;
    private String category;

}