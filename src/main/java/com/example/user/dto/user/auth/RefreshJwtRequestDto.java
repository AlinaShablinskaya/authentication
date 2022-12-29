package com.example.user.dto.user.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtRequestDto {
    public String refreshToken;
}
