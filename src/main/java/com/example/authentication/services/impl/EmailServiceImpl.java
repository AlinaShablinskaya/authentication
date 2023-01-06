package com.example.authentication.services.impl;

import com.example.authentication.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailServiceImpl implements EmailService {
    private static final String CONFIRMATION_REGISTRATION_URL = "http://localhost:8080/api/users/verify?token=";
    private static final String CONFIRMATION_PASSWORD_UPDATING_URL = "http://localhost:8080/api/users/recover?token=";

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
        public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendRegistrationConfirmationEmail(String recipient, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("Confirmation of registration");
        message.setText(CONFIRMATION_REGISTRATION_URL + token);

        javaMailSender.send(message);
    }

    public void sendResetPasswordEmail(String recipient, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("Reset Password");
        message.setText(CONFIRMATION_PASSWORD_UPDATING_URL + token);

        javaMailSender.send(message);
    }
}
