package com.bookshop.service.impl;

import com.bookshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail,
                             String fullName,
                             String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Book Shop - Email Verification OTP");

        message.setText(
                "Hello " + fullName + ",\n\n"
                        + "Your OTP for Book Shop registration is: "
                        + otp
                        + "\n\n"
                        + "This OTP will expire in 5 minutes."
                        + "\n\n"
                        + "If you did not request this OTP, please ignore this email."
                        + "\n\n"
                        + "Regards,\n"
                        + "Book Shop Team"
        );

        mailSender.send(message);
    }
}