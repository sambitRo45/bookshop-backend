package com.bookshop.repository;

import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // Find all items belonging to an order
    List<OrderItem> findByOrder(Order order);

    // Find a particular book inside an order
    Optional<OrderItem> findByOrderAndBook(
            Order order,
            Book book
    );

    // Find all order items for a particular book
    List<OrderItem> findByBook(Book book);

    // Find all purchased items belonging to a customer
    List<OrderItem> findByOrderUser(User user);

    // Check whether a customer has purchased a book
    boolean existsByOrderUserAndBook(
            User user,
            Book book
    );
}