package com.example.authentication.services.impl;

import com.example.authentication.dto.auth.AuthenticationDto;
import com.example.authentication.dto.auth.JwtResponseDto;
import com.example.authentication.entities.User;
import com.example.authentication.repositories.UserRepository;
import com.example.authentication.jwt.JwtTokenProvider;
import com.example.authentication.services.AuthenticationService;
import com.example.authentication.services.UserService;
import com.example.authentication.services.exception.UserAuthenticationProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String AUTHENTICATION_EXCEPTION = "Invalid username/password supplied";
    private static final String USER_NOT_FOUND_EXCEPTION = "Specified user by email is not found";
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    public JwtResponseDto authenticate(AuthenticationDto authenticationDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(),
                authenticationDto.getPassword()));
            User user = userService.getUserByMailIfExist(authenticationDto.getEmail());
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponseDto(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new UserAuthenticationProcessingException(AUTHENTICATION_EXCEPTION, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public JwtResponseDto recreateToken(String refreshToken) {
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String email = jwtTokenProvider.getLoginFromRefreshToken(refreshToken);
            String savedRefreshToken = refreshStorage.get(email);
            if (Objects.nonNull(savedRefreshToken) && savedRefreshToken.equals(refreshToken)) {
                User user = userService.getUserByMailIfExist(email);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication)) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            //delete token
        }
    }
}
