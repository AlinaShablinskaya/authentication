package com.example.user.services;

import com.example.user.dto.user.auth.AuthenticationDto;
import com.example.user.dto.user.auth.JwtResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AuthenticationService {
    JwtResponseDto authenticate(AuthenticationDto authenticationDto);

    JwtResponseDto getAccessToken(String refreshToken);

    JwtResponseDto getRefreshToken(String refreshToken);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
