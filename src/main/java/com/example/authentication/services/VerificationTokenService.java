package com.example.authentication.services;

import com.example.authentication.entities.User;
import com.example.authentication.entities.VerificationToken;

public interface VerificationTokenService {
    VerificationToken saveToken(User user);

    boolean validateVerificationToken(String token);

    boolean validateResetPasswordToken(String token);

    VerificationToken findByToken(String token);

    void removeToken(String token);
}
