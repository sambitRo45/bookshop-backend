package com.bookshop.entity;

import com.bookshop.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Password is already encrypted before storing.
     */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime otpExpiryTime;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}