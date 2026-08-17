package com.bookshop.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponseDto {

    private Long totalBooks;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long booksInStock;
    private Long outOfStockBooks;

}