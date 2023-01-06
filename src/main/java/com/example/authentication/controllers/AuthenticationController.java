package com.example.authentication.controllers;

import com.example.authentication.dto.auth.AuthenticationDto;
import com.example.authentication.dto.auth.JwtResponseDto;
import com.example.authentication.dto.auth.RefreshJwtRequestDto;
import com.example.authentication.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtResponseDto authenticate(@RequestBody AuthenticationDto authenticationDto) {
        return authenticationService.authenticate(authenticationDto);
    }

    @PostMapping("/token/access")
    public JwtResponseDto getNewAccessToken(@Valid @RequestBody RefreshJwtRequestDto refreshJwtDto) {
        return authenticationService.recreateToken(refreshJwtDto.getRefreshToken());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
    }
}
