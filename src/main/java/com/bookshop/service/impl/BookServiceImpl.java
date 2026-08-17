package com.bookshop.service.impl;

import com.bookshop.dto.book.BookCreateRequestDto;
import com.bookshop.dto.book.BookResponseDto;
import com.bookshop.dto.book.BookUpdateRequestDto;
import com.bookshop.entity.Book;
import com.bookshop.entity.User;
import com.bookshop.repository.BookRepository;
import com.bookshop.repository.UserRepository;
import com.bookshop.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;


    // =========================================================
    // CREATE BOOK
    // =========================================================

    @Override
    public BookResponseDto createBook(BookCreateRequestDto request) {

        /*
         * Get the currently logged-in shop keeper.
         */
        User shopKeeper = getCurrentUser();


        /*
         * Check whether ISBN already exists.
         */
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException(
                    "Book with ISBN " + request.getIsbn()
                            + " already exists."
            );
        }


        /*
         * Convert DTO to Entity.
         */
        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setQuantity(request.getQuantity());
        book.setImageUrl(request.getImageUrl());
        book.setPublishedDate(request.getPublishedDate());

        /*
         * Assign the logged-in shop keeper.
         */
        book.setShopKeeper(shopKeeper);


        /*
         * Save book.
         */
        Book savedBook = bookRepository.save(book);


        /*
         * Convert Entity to Response DTO.
         */
        return convertToResponse(savedBook);
    }


    // =========================================================
    // UPDATE BOOK
    // =========================================================

    @Override
    public BookResponseDto updateBook(
            Long bookId,
            BookUpdateRequestDto request
    ) {

        /*
         * Find book.
         */
        Book book = findBookById(bookId);


        /*
         * Check ownership.
         */
        User currentUser = getCurrentUser();

        checkOwnership(book, currentUser);


        /*
         * Check ISBN.
         *
         * If the ISBN is being changed,
         * make sure another book doesn't already use it.
         */
        if (!book.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {

            throw new RuntimeException(
                    "Book with ISBN " + request.getIsbn()
                            + " already exists."
            );
        }


        /*
         * Update fields.
         */
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setQuantity(request.getQuantity());
        book.setImageUrl(request.getImageUrl());
        book.setPublishedDate(request.getPublishedDate());


        /*
         * Save updated book.
         */
        Book updatedBook = bookRepository.save(book);


        return convertToResponse(updatedBook);
    }


    // =========================================================
    // DELETE BOOK
    // =========================================================

    @Override
    public void deleteBook(Long bookId) {

        Book book = findBookById(bookId);

        User currentUser = getCurrentUser();

        /*
         * Only the owner can delete the book.
         */
        checkOwnership(book, currentUser);

        bookRepository.delete(book);
    }


    // =========================================================
    // GET OWN BOOKS
    // =========================================================

    @Override
    public List<BookResponseDto> getOwnBooks() {

        User currentUser = getCurrentUser();

        List<Book> books =
                bookRepository.findByShopKeeper(currentUser);

        return books.stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET ALL BOOKS
    // =========================================================

    @Override
    public List<BookResponseDto> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET BOOK BY ID
    // =========================================================

    @Override
    public BookResponseDto getBookById(Long bookId) {

        Book book = findBookById(bookId);

        return convertToResponse(book);
    }


    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    @Override
    public List<BookResponseDto> searchByTitle(String title) {

        return bookRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // SEARCH BY AUTHOR
    // =========================================================

    @Override
    public List<BookResponseDto> searchByAuthor(String author) {

        return bookRepository
                .findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // SEARCH BY CATEGORY
    // =========================================================

    @Override
    public List<BookResponseDto> searchByCategory(String category) {

        return bookRepository
                .findByCategoryContainingIgnoreCase(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // FIND BOOK
    // =========================================================

    private Book findBookById(Long bookId) {

        return bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: " + bookId
                        )
                );
    }


    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        String email =
                org.springframework.security.core.context
                        .SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found."
                        )
                );
    }


    // =========================================================
    // CHECK BOOK OWNERSHIP
    // =========================================================

    private void checkOwnership(
            Book book,
            User currentUser
    ) {

        if (!book.getShopKeeper()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not authorized to modify this book."
            );
        }
    }


    // =========================================================
    // ENTITY → DTO
    // =========================================================

    private BookResponseDto convertToResponse(Book book) {

        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getQuantity(),
                book.getImageUrl(),
                book.getPublishedDate(),
                book.getShopKeeper().getId(),
                book.getShopKeeper().getFullName()
        );
    }
}