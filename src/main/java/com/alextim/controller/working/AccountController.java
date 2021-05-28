package com.alextim.controller.working;

import com.alextim.controller.dto.MessageDto;
import com.alextim.controller.dto.UserDto;
import com.alextim.domain.User;
import com.alextim.service.gateway.EmailService;
import com.alextim.service.gateway.JwtService;
import com.alextim.service.security.SecurityService;
import com.alextim.service.working.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.alextim.controller.dto.UserDto.JSON_EXAMPLE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/account")
@RequiredArgsConstructor @Slf4j
public class AccountController {

    @Value("${application.url}")
    public String url;

    @Value("${application.emailName}")
    public String emailName;

    private final UserService userService;

    private final JwtService jwtService;

    private final EmailService serviceEmail;

    private final SecurityService securityService;

    @PostMapping()
    public MessageDto accountCreationRequest(@Valid @RequestBody UserDto userDto, BindingResult result,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        if(result.hasErrors()) {
            System.out.println(" = " );
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        if(userService.findByEmail(userDto.getEmail()) != null || userService.findByUsername(userDto.getUsername()) != null) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Email or username is not unique");
        }

        serviceEmail.send(userDto.getEmail(), emailName,url + "/account/parser?jwt=" + jwtService.createJwt(userDto));
        response.setStatus(SC_OK);
        return new MessageDto("Check email: " + userDto.getEmail());
    }

    @GetMapping("/parser")
    public MessageDto parser(@RequestParam String jwt,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        UserDto userDto = jwtService.parser(jwt);

        User user = userService.add(
                userDto.getUsername(),
                userDto.getName(),
                userDto.getSurname(),
                userDto.getEmail(),
                userDto.getRawPassword());

        securityService.addSecurity(user.getUsername(), user.getId(), User.class);

        response.setStatus(SC_OK);
        log.info("{} saved", userDto.getUsername());
        return new MessageDto(userDto.getUsername() + " saved");
    }
}
