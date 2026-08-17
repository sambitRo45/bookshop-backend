package com.bookshop.repository;

import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.ReturnRequest;
import com.bookshop.enums.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestRepository
        extends JpaRepository<ReturnRequest, Long> {

    // Find all return requests for an order
    List<ReturnRequest> findByOrder(Order order);

    // Find returns for a particular book
    List<ReturnRequest> findByBook(Book book);

    // Find returns by status
    List<ReturnRequest> findByReturnStatus(
            ReturnStatus returnStatus
    );

    // Find returns for a particular order and book
    List<ReturnRequest> findByOrderAndBook(
            Order order,
            Book book
    );
}