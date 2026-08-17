package com.bookshop.service.impl;

import com.bookshop.config.RazorpayConfig;
import com.bookshop.dto.payment.CreatePaymentResponseDto;
import com.bookshop.dto.payment.PaymentResponseDto;
import com.bookshop.dto.payment.PaymentVerifyRequestDto;
import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.Payment;
import com.bookshop.entity.User;
import com.bookshop.enums.OrderStatus;
import com.bookshop.enums.PaymentStatus;
import com.bookshop.repository.BookRepository;
import com.bookshop.repository.OrderRepository;
import com.bookshop.repository.PaymentRepository;
import com.bookshop.repository.UserRepository;
import com.bookshop.service.PaymentService;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final RazorpayConfig razorpayConfig;

    private final BookRepository bookRepository;


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @Override
    @Transactional
    public CreatePaymentResponseDto createRazorpayOrder(
            Long orderId
    ) {

        /*
         * Get currently authenticated customer.
         */
        User customer = getCurrentUser();


        /*
         * Find our internal order.
         */
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: "
                                        + orderId
                        )
                );


        /*
         * Make sure this order belongs to
         * the currently logged-in customer.
         */
        if (!order.getUser()
                .getId()
                .equals(customer.getId())) {

            throw new RuntimeException(
                    "You are not authorized to pay for this order."
            );
        }


        /*
         * Payment should only be created for
         * a PENDING order.
         */
        if (order.getOrderStatus()
                != OrderStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending orders can be paid."
            );
        }


        /*
         * Check whether a payment record already
         * exists for this order.
         */
        if (paymentRepository.findByOrder(order)
                .isPresent()) {

            throw new RuntimeException(
                    "Payment has already been initiated for this order."
            );
        }


        try {

            /*
             * Create Razorpay client.
             *
             * IMPORTANT:
             * key secret stays on backend.
             */
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayConfig.getKeyId(),
                            razorpayConfig.getKeySecret()
                    );


            /*
             * Convert amount from Rupees to Paise.
             *
             * Example:
             *
             * ₹1598
             *
             * 1598 × 100
             *
             * = 159800 paise
             */
            long amountInPaise =
                    order.getTotalAmount()
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .longValueExact();


            /*
             * Create Razorpay request.
             */
            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            orderRequest.put(
                    "receipt",
                    order.getOrderNumber()
            );


            /*
             * Create order on Razorpay.
             */
            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(
                            orderRequest
                    );


            /*
             * Get Razorpay Order ID.
             *
             * Example:
             *
             * order_S8abc123
             */
            String razorpayOrderId =
                    razorpayOrder.get("id");


            /*
             * Create Payment entity.
             */
            Payment payment = new Payment();

            payment.setOrder(order);

            payment.setRazorpayOrderId(
                    razorpayOrderId
            );

            payment.setAmount(
                    order.getTotalAmount()
            );

            payment.setPaymentStatus(
                    PaymentStatus.CREATED
            );


            /*
             * Save payment in database.
             */
            paymentRepository.save(payment);


            /*
             * Return only the information
             * frontend actually needs.
             *
             * NEVER return key secret.
             */
            return new CreatePaymentResponseDto(
                    razorpayOrderId,
                    razorpayConfig.getKeyId(),
                    order.getTotalAmount(),
                    "INR",
                    order.getOrderNumber()
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Failed to create Razorpay order: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @Override
    @Transactional
    public PaymentResponseDto verifyPayment(
            PaymentVerifyRequestDto request
    ) {

        /*
         * Find payment using Razorpay Order ID.
         */
        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment record not found."
                                )
                        );


        /*
         * Get our internal order.
         */
        Order order = payment.getOrder();


        /*
         * Payment must not already be successful.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            throw new RuntimeException(
                    "Payment has already been verified."
            );
        }


        /*
         * Verify that the Razorpay order ID
         * sent by the frontend matches our database.
         */
        if (!payment.getRazorpayOrderId()
                .equals(request.getRazorpayOrderId())) {

            throw new RuntimeException(
                    "Invalid Razorpay order ID."
            );
        }


        try {

            /*
             * Create the signature data required by Razorpay.
             *
             * Razorpay verifies:
             *
             * razorpay_order_id
             * +
             * "|"
             * +
             * razorpay_payment_id
             */
            String signatureData =
                    request.getRazorpayOrderId()
                            + "|"
                            + request.getRazorpayPaymentId();


            /*
             * Verify Razorpay signature using
             * the secret key.
             */
            boolean signatureValid =
                    com.razorpay.Utils.verifySignature(
                            signatureData,
                            request.getRazorpaySignature(),
                            razorpayConfig.getKeySecret()
                    );


            /*
             * Invalid signature means we must
             * NOT confirm the order.
             */
            if (!signatureValid) {

                payment.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                paymentRepository.save(payment);

                throw new RuntimeException(
                        "Invalid Razorpay payment signature."
                );
            }


            /*
             * Make sure the order is still pending.
             */
            if (order.getOrderStatus()
                    != OrderStatus.PENDING) {

                throw new RuntimeException(
                        "Order is not in pending status."
                );
            }


            /*
             * Process every book in the order.
             */
            for (var orderItem : order.getOrderItems()) {

                /*
                 * IMPORTANT:
                 *
                 * Fetch the book using a database lock.
                 */
                Book book =
                        bookRepository
                                .findByIdWithLock(
                                        orderItem.getBook().getId()
                                )
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Book not found with id: "
                                                        + orderItem
                                                        .getBook()
                                                        .getId()
                                        )
                                );


                /*
                 * Check current stock again.
                 *
                 * We must NOT rely only on the
                 * stock check performed when the
                 * order was originally created.
                 */
                if (book.getQuantity()
                        < orderItem.getQuantity()) {

                    payment.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    paymentRepository.save(payment);

                    throw new RuntimeException(
                            "Insufficient stock for book: "
                                    + book.getTitle()
                    );
                }


                /*
                 * Reduce available quantity.
                 */
                book.setQuantity(
                        book.getQuantity()
                                - orderItem.getQuantity()
                );


                /*
                 * Save updated book stock.
                 */
                bookRepository.save(book);
            }


            /*
             * Payment was successfully verified.
             */
            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );

            payment.setPaymentStatus(
                    PaymentStatus.SUCCESS
            );


            paymentRepository.save(payment);


            /*
             * Confirm the order.
             */
            order.setOrderStatus(
                    OrderStatus.CONFIRMED
            );

            orderRepository.save(order);


            /*
             * Return payment information.
             */
            return new PaymentResponseDto(
                    payment.getId(),
                    order.getId(),
                    order.getOrderNumber(),
                    payment.getRazorpayOrderId(),
                    payment.getRazorpayPaymentId(),
                    payment.getAmount(),
                    payment.getPaymentStatus()
            );

        } catch (RuntimeException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Payment verification failed: "
                            + exception.getMessage(),
                    exception
            );
        }
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


        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found."
                        )
                );
    }
}