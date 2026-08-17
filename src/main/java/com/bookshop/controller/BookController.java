package com.bookshop.controller;

import com.bookshop.dto.book.BookResponseDto;
import com.bookshop.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    // =========================================================
    // GET ALL BOOKS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {

        List<BookResponseDto> books =
                bookService.getAllBooks();

        return ResponseEntity.ok(books);
    }


    // =========================================================
    // GET BOOK BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(
            @PathVariable Long id
    ) {

        BookResponseDto book =
                bookService.getBookById(id);

        return ResponseEntity.ok(book);
    }


    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponseDto>> searchByTitle(
            @RequestParam String title
    ) {

        List<BookResponseDto> books =
                bookService.searchByTitle(title);

        return ResponseEntity.ok(books);
    }


    // =========================================================
    // SEARCH BY AUTHOR
    // =========================================================

    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponseDto>> searchByAuthor(
            @RequestParam String author
    ) {

        List<BookResponseDto> books =
                bookService.searchByAuthor(author);

        return ResponseEntity.ok(books);
    }


    // =========================================================
    // SEARCH BY CATEGORY
    // =========================================================

    @GetMapping("/search/category")
    public ResponseEntity<List<BookResponseDto>> searchByCategory(
            @RequestParam String category
    ) {

        List<BookResponseDto> books =
                bookService.searchByCategory(category);

        return ResponseEntity.ok(books);
    }
}