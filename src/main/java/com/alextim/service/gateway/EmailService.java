package com.alextim.service.gateway;

public interface EmailService {
    void send(String to, String subject, String text);
}
