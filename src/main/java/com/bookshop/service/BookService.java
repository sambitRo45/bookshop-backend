package com.bookshop.service;

import com.bookshop.dto.book.BookCreateRequestDto;
import com.bookshop.dto.book.BookResponseDto;
import com.bookshop.dto.book.BookUpdateRequestDto;

import java.util.List;

public interface BookService {

    BookResponseDto createBook(BookCreateRequestDto request);

    BookResponseDto updateBook(Long bookId, BookUpdateRequestDto request);

    void deleteBook(Long bookId);

    List<BookResponseDto> getOwnBooks();

    List<BookResponseDto> getAllBooks();

    BookResponseDto getBookById(Long bookId);

    List<BookResponseDto> searchByTitle(String title);

    List<BookResponseDto> searchByAuthor(String author);

    List<BookResponseDto> searchByCategory(String category);
}