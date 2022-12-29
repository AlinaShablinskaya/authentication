package com.example.user.controllers;

import com.example.user.dto.user.auth.AuthenticationDto;
import com.example.user.dto.user.auth.JwtResponseDto;
import com.example.user.dto.user.auth.RefreshJwtRequestDto;
import com.example.user.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtResponseDto authenticate(@RequestBody AuthenticationDto authenticationDto) {
        return authenticationService.authenticate(authenticationDto);
    }

    @PostMapping("/token/access")
    public JwtResponseDto getNewAccessToken(@RequestBody RefreshJwtRequestDto refreshJwtDto) {
        return authenticationService.getAccessToken(refreshJwtDto.getRefreshToken());
    }

    @PostMapping("/token/refresh")
    public JwtResponseDto getNewRefreshToken(@RequestBody RefreshJwtRequestDto refreshJwtDto) {
        return authenticationService.getRefreshToken(refreshJwtDto.getRefreshToken());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
    }
}
