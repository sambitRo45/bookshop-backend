package com.bookshop.service.impl;

import com.bookshop.dto.order.OrderCreateRequestDto;
import com.bookshop.dto.order.OrderItemRequestDto;
import com.bookshop.dto.order.OrderItemResponseDto;
import com.bookshop.dto.order.OrderResponseDto;
import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.entity.User;
import com.bookshop.enums.OrderStatus;
import com.bookshop.repository.BookRepository;
import com.bookshop.repository.OrderItemRepository;
import com.bookshop.repository.OrderRepository;
import com.bookshop.repository.UserRepository;
import com.bookshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Override
    @Transactional
    public OrderResponseDto createOrder(
            OrderCreateRequestDto request
    ) {

        /*
         * Get currently logged-in customer.
         */
        User customer = getCurrentUser();


        /*
         * Create new Order.
         */
        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());

        order.setUser(customer);

        order.setOrderStatus(OrderStatus.PENDING);

        /*
         * Total starts from zero.
         */
        BigDecimal totalAmount = BigDecimal.ZERO;


        /*
         * Store OrderItems.
         */
        List<OrderItem> orderItems = new ArrayList<>();


        // =====================================================
        // PROCESS EACH BOOK
        // =====================================================

        for (OrderItemRequestDto itemRequest : request.getItems()) {

            /*
             * Find book.
             */
            Book book = bookRepository
                    .findById(itemRequest.getBookId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Book not found with id: "
                                            + itemRequest.getBookId()
                            )
                    );


            /*
             * Check stock.
             */
            if (book.getQuantity()
                    < itemRequest.getQuantity()) {

                throw new RuntimeException(
                        "Not enough stock for book: "
                                + book.getTitle()
                );
            }


            /*
             * Get current book price.
             */
            BigDecimal price = book.getPrice();


            /*
             * Calculate subtotal.
             */
            BigDecimal subtotal =
                    price.multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getQuantity()
                            )
                    );


            /*
             * Add subtotal to total.
             */
            totalAmount =
                    totalAmount.add(subtotal);


            /*
             * Create OrderItem.
             */
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setBook(book);

            orderItem.setQuantity(
                    itemRequest.getQuantity()
            );

            /*
             * Store price snapshot.
             */
            orderItem.setPrice(price);


            orderItems.add(orderItem);
        }


        // =====================================================
        // SET ORDER TOTAL
        // =====================================================

        order.setTotalAmount(totalAmount);

        order.setOrderItems(orderItems);


        /*
         * Save order.
         *
         * Because Order has:
         *
         * cascade = CascadeType.ALL
         *
         * the OrderItems will also be saved.
         */
        Order savedOrder =
                orderRepository.save(order);


        return convertToResponse(savedOrder);
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(
            Long orderId
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found with id: "
                                                + orderId
                                )
                        );


        User currentUser = getCurrentUser();


        /*
         * A customer can only view their own order.
         *
         * Shopkeeper access will be handled separately
         * through the shopkeeper order API.
         */
        if (!order.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not authorized to view this order."
            );
        }


        return convertToResponse(order);
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders() {

        User customer = getCurrentUser();


        List<Order> orders =
                orderRepository
                        .findByUserOrderByCreatedAtDesc(
                                customer
                        );


        return orders.stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        String email =
                SecurityContextHolder
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
    // GENERATE ORDER NUMBER
    // =========================================================

    private String generateOrderNumber() {

        return "ORD-"
                + System.currentTimeMillis()
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private OrderResponseDto convertToResponse(
            Order order
    ) {

        List<OrderItemResponseDto> itemResponses =
                order.getOrderItems()
                        .stream()
                        .map(this::convertItemToResponse)
                        .toList();


        return new OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }


    // =========================================================
    // ORDER ITEM → RESPONSE DTO
    // =========================================================

    private OrderItemResponseDto convertItemToResponse(
            OrderItem orderItem
    ) {

        BigDecimal subtotal =
                orderItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        orderItem.getQuantity()
                                )
                        );


        return new OrderItemResponseDto(
                orderItem.getId(),
                orderItem.getBook().getId(),
                orderItem.getBook().getTitle(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                subtotal
        );
    }
}