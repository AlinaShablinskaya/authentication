package com.example.authentication.services.impl;

import com.example.authentication.entities.User;
import com.example.authentication.entities.VerificationToken;
import com.example.authentication.entities.enums.Status;
import com.example.authentication.repositories.VerificationTokenRepository;
import com.example.authentication.services.VerificationTokenService;
import com.example.authentication.services.exception.VerificationUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = StandardCharsets.US_ASCII;
    private static final String TOKEN_NOT_FOUND_EXCEPTION = "Specified token is not found";
    private static final String TOKEN_IS_EXPIRED_EXCEPTION = "Token is expired";
    private static final String USER_ACTIVE_EXCEPTION = "User is already active";
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken saveToken(User user) {
        VerificationToken verificationToken = generateToken(user);
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElseThrow(() ->
                new EntityNotFoundException(TOKEN_NOT_FOUND_EXCEPTION));
    }

    @Override
    public boolean validateVerificationToken(String token) {
        VerificationToken verificationToken = findByToken(token);
        validateTokenByExpirationDate(verificationToken);
        validateTokenByUserStatus(verificationToken);
        return true;
    }

    @Override
    public boolean validateResetPasswordToken(String token) {
        VerificationToken verificationToken = findByToken(token);
        validateTokenByExpirationDate(verificationToken);
        return true;
    }

    private void validateTokenByExpirationDate(VerificationToken verificationToken) {
        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new VerificationUserException(TOKEN_IS_EXPIRED_EXCEPTION);
        }
    }

    private void validateTokenByUserStatus(VerificationToken verificationToken) {
       User user = verificationToken.getUser();
        if (user.getStatus().equals(Status.ACTIVE)) {
            throw new VerificationUserException(USER_ACTIVE_EXCEPTION);
        }
    }

    @Override
    public void removeToken(String token) {
        verificationTokenRepository.deleteByToken(token);
    }

    private VerificationToken generateToken(User user) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExpiration = now.plusDays(5);
        return VerificationToken.builder()
                .withToken(tokenValue)
                .withUser(user)
                .withExpirationDate(accessExpiration)
                .build();
    }
}
