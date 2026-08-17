package com.bookshop.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
