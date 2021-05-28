package com.alextim.service.gateway;

import com.alextim.controller.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

import static com.alextim.domain.User.*;

@Service
@RequiredArgsConstructor @Slf4j
public class JwtService {

    @Value("${application.hourValidateTokenForRegistration}")
    private int hourValidateTokenForRegistration;

    private final SecretService secretService;

    public String createJwt(UserDto userDto) {
        String jwt = Jwts.builder()
                .setIssuer("MySuperService")
                .setSubject("Check email")
                .claim(COLUMN_USER_NAME, userDto.getUsername())
                .claim(COLUMN_NAME, userDto.getName())
                .claim(COLUMN_SURNAME, userDto.getSurname())
                .claim(COLUMN_EMAIL, userDto.getEmail())
                .claim(COLUMN_PASSWORD, userDto.getRawPassword())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(SignatureAlgorithm.HS256, secretService.getHS256SecretBytes())
                .compact();

        log.info("jwt created from user: {}", userDto);
        return jwt;
    }

    private Date getExpirationDate() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, instance.get(Calendar.HOUR) + hourValidateTokenForRegistration);
        return instance.getTime();
    }

    public UserDto parser(String jwt) {
        Claims body = Jwts.parser()
                .setSigningKeyResolver(secretService.getSigningKeyResolver())
                .parseClaimsJws(jwt)
                .getBody();

        UserDto userDto = new UserDto( (String)body.get(COLUMN_USER_NAME),
                                    (String)body.get(COLUMN_NAME),
                                    (String)body.get(COLUMN_SURNAME),
                                    (String)body.get(COLUMN_EMAIL),
                                    (String)body.get(COLUMN_PASSWORD));

        log.info("user from jwt: {}", userDto);
        return userDto;
    }
}