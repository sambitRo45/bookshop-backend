package com.bookshop.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String fullName, String otp);

}