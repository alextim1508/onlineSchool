package com.alextim.security.configuration;

import com.alextim.service.working.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return UserDetailsServiceImpl.encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPasswordFromStorage) {
        log.info("raw password sent by user: " + rawPassword + ", encoded password from storage: " + encodedPasswordFromStorage);
        return encode(rawPassword).equals(encodedPasswordFromStorage);
    }
}