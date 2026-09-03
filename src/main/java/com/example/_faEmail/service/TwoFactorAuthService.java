package com.example._faEmail.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TwoFactorAuthService {

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.mail.username}")
    private String fromEmail;

    public TwoFactorAuthService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public String generate2FaCode() {
        int code = secureRandom.nextInt(1000000);
        return String.format("%06d", code);
    }

    @Async
    public void sendVerificationEmail(String userEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(userEmail);
        message.setSubject("Please confirm your account");

        message.setText("Hi!\n\n" +
                "Thank you for registration on our site, Here`s your verification code: " + code + "\n\n" +
                "Code will expire in 5 minutes, please make sure to use it on time.");

        mailSender.send(message);
    }
}
