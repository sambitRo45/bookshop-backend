package com.bookshop.controller;

import com.bookshop.dto.book.BookCreateRequestDto;
import com.bookshop.dto.book.BookResponseDto;
import com.bookshop.dto.book.BookUpdateRequestDto;
import com.bookshop.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopkeeper/books")
@RequiredArgsConstructor
public class ShopKeeperBookController {

    private final BookService bookService;


    // =========================================================
    // ADD BOOK
    // =========================================================

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestBody BookCreateRequestDto request
    ) {

        BookResponseDto response =
                bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // UPDATE BOOK
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequestDto request
    ) {

        BookResponseDto response =
                bookService.updateBook(id, request);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE BOOK
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // VIEW OWN BOOKS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getOwnBooks() {

        List<BookResponseDto> books =
                bookService.getOwnBooks();

        return ResponseEntity.ok(books);
    }
}