package com.alextim.service.gateway;

public interface SmsService {
    String generationSms();
    void sendSms(String phoneNumber, String sms);
}
