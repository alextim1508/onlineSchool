package com.alextim.controller.working;

import com.alextim.controller.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/public")
    public MessageDto publicData(HttpServletRequest request,
                                 HttpServletResponse response) {
        return new MessageDto("public");
    }

    @GetMapping("/authenticated")
    public MessageDto sucurityData(HttpServletRequest request,
                                    HttpServletResponse response) {
        return new MessageDto("authenticated only");
    }

    @GetMapping("/login")
    public MessageDto loginPage(HttpServletRequest request,
                                HttpServletResponse response) {
        response.setStatus(SC_OK);
        return new MessageDto("Unauthorized. Have come post request witch username, password to '/login'.");
    }

    @GetMapping("/")
    public MessageDto rootPage(HttpServletRequest request,
                                HttpServletResponse response) {
        response.setStatus(SC_OK);
        return new MessageDto("Hello, " + getStringAuthentication());
    }

    @PostMapping("/success1")
    public MessageDto successPrimaryAuthenticationPage(HttpServletRequest request,
                                                       HttpServletResponse response) {
        response.setStatus(SC_OK);
        return new MessageDto("PrimaryAuthenticationToken Ok. Have come post request witch username, sms to '/login'.");
    }

    @PostMapping("/success2")
    public MessageDto successSecondaryAuthenticationPage(HttpServletRequest request,
                                                         HttpServletResponse response) {
        response.setStatus(SC_OK);
        return new MessageDto("SecondaryAuthenticationToken Ok. Authorized.");
    }

    @PostMapping("/success")
    public MessageDto successUsernamePasswordAuthenticationPage(HttpServletRequest request,
                                                       HttpServletResponse response) {
        response.setStatus(SC_OK);
        return new MessageDto("UsernamePasswordAuthenticationToken Ok.");
    }

//    @PostMapping("/error")
//    public MessageDto errorPage(HttpServletRequest request,
//                                HttpServletResponse response) {
//        return new MessageDto("Error login or password.");
//    }

    private String getStringAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().toString();
    }
}
