package com.example.authentication.services;

public interface EmailService {

    void sendRegistrationConfirmationEmail(String recipient, String token);

    void sendResetPasswordEmail(String recipient, String token);
}
