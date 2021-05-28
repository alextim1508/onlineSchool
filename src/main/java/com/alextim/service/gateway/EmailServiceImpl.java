package com.alextim.service.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        }
        catch (Exception ex) {
            log.error("SendEmailException: {}", ex.getMessage());
            throw new RuntimeException("Send email error");
        }

        log.info("Email sent to {}", to);
    }
}