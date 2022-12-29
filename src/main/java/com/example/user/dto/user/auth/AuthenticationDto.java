package com.example.user.dto.user.auth;

import lombok.Data;

@Data
public class AuthenticationDto {
    private String email;
    private String password;
}
