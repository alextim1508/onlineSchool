package com.alextim.controller;

import com.alextim.controller.dto.UserDto;
import com.alextim.domain.User;
import com.alextim.service.gateway.EmailService;
import com.alextim.service.gateway.JwtService;
import com.alextim.service.security.SecurityService;
import com.alextim.service.working.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Value("${application.url}")
    public String url;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Before
    public void initMock() throws Exception {
        when(userService.findByEmail(any(String.class))).thenAnswer(invocation-> {
            Object[] args = invocation.getArguments();
            if(args[0].equals("Alex@ya.ru"))
                return new User();
            return null;
        });

        when(userService.findByUsername(any(String.class))).thenAnswer(invocation-> {
            Object[] args = invocation.getArguments();
            if(args[0].equals("Alex"))
                return new User();
            return null;
        });

        when(jwtService.createJwt(any(UserDto.class))).thenReturn("superToken");

        doNothing().when(emailService).send(any(String.class), any(String.class), any(String.class));
    }


    @Test
    public void accountCreationRequestTest() throws Exception {
        String username = "IvanPetrov";
        String name = "Ivan";
        String surname = "Petrov";
        String email = "ivanpetrov@gmail.com";
        String rawPassword = "123";

        String content = getContent(username, name, surname, email, rawPassword);

        mvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().is(SC_OK))
                .andExpect(jsonPath("$.message").value("Check email: " + email));
    }

    @Test
    public void badAccountCreationWithNotUniqueUsername() throws Exception {
        String username = "Alex";
        String name = "Alex";
        String surname = "Petrov";
        String email = "ivanpetrov@gmail.com";
        String rawPassword = "123";

        String content = getContent(username, name, surname, email, rawPassword);

        mvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().is(SC_BAD_REQUEST))
                .andExpect(jsonPath("$.message").value("Email or username is not unique"));
    }

    @Test
    public void badAccountCreationWithNotUniqueEmail() throws Exception {
        String username = "AlexTim";
        String name = "Alex";
        String surname = "Petrov";
        String email = "Alex@ya.ru";
        String rawPassword = "123";

        String content = getContent(username, name, surname, email, rawPassword);

        mvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().is(SC_BAD_REQUEST))
                .andExpect(jsonPath("$.message").value("Email or username is not unique"));
    }

    private String getContent(String username, String name, String surname, String email, String rawPassword) {
        return
                "{ " +
                (username != null ? ("\"username\" : \""  + username + "\", ") : "") +
                (name!= null ? ("\"name\" : \"" + name + "\", ") : "")   +
                (surname!=null ? ("\"surname\" : \"" + surname + "\", ")  : "") +
                (email!=null ? ("\"email\" : \"" + email + "\", ") : "") +
                (rawPassword!=null ? ("\"rawPassword\" : \"" + rawPassword) : "") +
                "\" }";
    }
}
