package com.alextim.service.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Random;

@Slf4j
@Service
public class SmscRuServiceImpl implements SmsService {

    @Value("${application.sms.mock}")
    public boolean isMock;

    @Value("${application.sms.login}")
    public String login;

    @Value("${application.sms.password}")
    public String password;

    @Override
    public void sendSms(String phoneNumber, String sms) {
        final String URL = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("smsc.ru/sys/send.php")
                .queryParam("login", login)
                .queryParam("psw", password)
                .queryParam("phones", phoneNumber)
                .queryParam("mes", sms)
                .build().toUriString();
        log.info("Sms {} send to number {}. Url sms service: {}", sms, phoneNumber, URL);

        String replySendSms;

        if(!isMock)
            replySendSms = new RestTemplate().getForObject(URL, String.class);
        else
            replySendSms = "OK";

        if(replySendSms.contains("OK"))
            log.info("Sms sent successfully! (Mock: {})", isMock);
        else
            log.warn("Sms not send! Reply: {}", replySendSms);
    }

    @Override
    public String generationSms() {
        return String.valueOf(new Random().nextInt(100));
    }
}

