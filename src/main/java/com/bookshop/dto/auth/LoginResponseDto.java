package com.bookshop.dto.auth;

import com.bookshop.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private Role role;

}