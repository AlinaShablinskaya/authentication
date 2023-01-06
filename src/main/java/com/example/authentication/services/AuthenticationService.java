package com.example.authentication.services;

import com.example.authentication.dto.auth.AuthenticationDto;
import com.example.authentication.dto.auth.JwtResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    JwtResponseDto authenticate(AuthenticationDto authenticationDto);

    JwtResponseDto recreateToken(String refreshToken);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
