package com.example.authentication.dto.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RefreshJwtRequestDto {
    @NotBlank
    public String refreshToken;
}
