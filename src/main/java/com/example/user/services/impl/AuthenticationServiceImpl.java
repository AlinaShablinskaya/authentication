package com.example.user.services.impl;

import com.example.user.dto.user.auth.AuthenticationDto;
import com.example.user.dto.user.auth.JwtResponseDto;
import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;
import com.example.user.security.jwt.JwtTokenProvider;
import com.example.user.services.AuthenticationService;
import com.example.user.services.exception.UserAuthenticationProcessingException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String AUTHENTICATION_EXCEPTION = "Invalid username/password supplied";
    private static final String USER_NOT_FOUND_EXCEPTION = "Specified user by email is not found";
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponseDto authenticate(AuthenticationDto authenticationDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(),
                authenticationDto.getPassword()));
            User user = getUserByMailIfExist(authenticationDto.getEmail());
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponseDto(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new UserAuthenticationProcessingException(AUTHENTICATION_EXCEPTION, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public JwtResponseDto getAccessToken(String refreshToken) {
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String email = jwtTokenProvider.getLoginFromRefreshToken(refreshToken);
            String savedRefreshToken = refreshStorage.get(email);
            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                User user = getUserByMailIfExist(email);
                String accessToken = jwtTokenProvider.generateAccessToken(user);
                return new JwtResponseDto(accessToken, null);
            }
        }
        return new  JwtResponseDto(null, null);
    }

    @Override
    public JwtResponseDto getRefreshToken(String refreshToken) {
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String email = jwtTokenProvider.getLoginFromRefreshToken(refreshToken);
            String savedRefreshToken = refreshStorage.get(email);
            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                User user = getUserByMailIfExist(email);
                String accessToken = jwtTokenProvider.generateAccessToken(user);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), newRefreshToken);
                return new JwtResponseDto(accessToken, newRefreshToken);
            }
        }
        throw new UserAuthenticationProcessingException(AUTHENTICATION_EXCEPTION, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
        contextLogoutHandler.logout(request, response, null);
    }

    private User getUserByMailIfExist(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(USER_NOT_FOUND_EXCEPTION));
    }
}
