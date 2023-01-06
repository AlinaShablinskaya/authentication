package com.example.authentication.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;
}
