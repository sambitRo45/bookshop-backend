package com.bookshop.repository;

import com.bookshop.entity.Book;
import com.bookshop.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Find all books belonging to a particular shop keeper
    List<Book> findByShopKeeper(User shopKeeper);

    // Search books by title
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Search books by author
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Search books by category
    List<Book> findByCategoryContainingIgnoreCase(String category);

    // Check whether ISBN already exists
    boolean existsByIsbn(String isbn);

    // Find book by ISBN
    Optional<Book> findByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithLock(@Param("id") Long id);
}