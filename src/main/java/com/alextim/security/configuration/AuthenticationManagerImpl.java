package com.alextim.security.configuration;

import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;
import com.alextim.security.token.PrimaryAuthenticationToken;
import com.alextim.security.token.SecondaryAuthenticationToken;
import com.alextim.service.gateway.SmsService;
import com.alextim.service.working.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor @Slf4j
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final UserDetailsService userDetailsService;

    private final SmsService smsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication newAuthentication = authentication;
        if(authentication.getClass().equals(UsernamePasswordAuthenticationToken.class) && !authentication.isAuthenticated()) {
            User user = getUserByAuthentication(authentication);

            if(passwordEncoder.matches((CharSequence)authentication.getCredentials(), user.getPassword())) {
                Set<GrantedAuthorityImpl> authorities = user.getAuthorities();
                if(authorities.contains(new GrantedAuthorityImpl(GrantedAuthorityImpl.Role.ADMIN))) {
                    newAuthentication = new PrimaryAuthenticationToken(user, "[PROTECTED]");
                    log.info("Successful primary authentication: {}", newAuthentication);
                    String sms = smsService.generationSms();
                    smsService.sendSms(user.getPhone(), sms);
                    userService.setSms(user.getId(), sms);
                }
                else{
                    newAuthentication = new UsernamePasswordAuthenticationToken(user, "[PROTECTED]", user.getAuthorities());
                    log.info("Successful usernamePassword authentication: {}", newAuthentication);
                }
            }
            else {
                log.warn("PrimaryAuthenticationException or UsernamePasswordAuthenticationException by {}", authentication);
                throw new BadCredentialsException("PrimaryAuthenticationException or UsernamePasswordAuthenticationException by " + authentication);
            }
        }
        if(authentication.getClass().equals(SecondaryAuthenticationToken.class) && !authentication.isAuthenticated()) {
            User user = getUserByAuthentication(authentication);
            if (authentication.getCredentials().equals(user.getSms())) {
                newAuthentication = new SecondaryAuthenticationToken(user, "[PROTECTED]", user.getAuthorities());
                log.info("Successful secondary authentication: {}", newAuthentication);
            }
            else {
                log.warn("SecondaryAuthenticationException by {}", authentication);
                throw new BadCredentialsException("SecondaryAuthenticationException by " + authentication);
            }
        }
        return newAuthentication;
    }

    private User getUserByAuthentication(Authentication authentication) {
        UserDetails userDetails = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
        return (User)userDetails;
    }
}