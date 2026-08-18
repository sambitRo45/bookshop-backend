package com.bookshop.service.impl;

import com.bookshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.brevo.com")
            .build();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Override
    public void sendOtpEmail(String toEmail,
                             String fullName,
                             String otp) {

        String emailContent =
                "<html>" +
                "<body>" +
                "<h2>Book Shop - Email Verification</h2>" +
                "<p>Hello " + fullName + ",</p>" +
                "<p>Your OTP for Book Shop registration is:</p>" +
                "<h2>" + otp + "</h2>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<p>If you did not request this OTP, please ignore this email.</p>" +
                "<br>" +
                "<p>Regards,<br>Book Shop Team</p>" +
                "</body>" +
                "</html>";

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", new Object[]{
                        Map.of(
                                "email", toEmail,
                                "name", fullName
                        )
                },
                "subject", "Book Shop - Email Verification OTP",
                "htmlContent", emailContent
        );

        restClient.post()
                .uri("/v3/smtp/email")
                .header("api-key", brevoApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
