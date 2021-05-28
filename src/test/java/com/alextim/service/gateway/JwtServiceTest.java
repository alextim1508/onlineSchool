package com.alextim.service.gateway;

import com.alextim.controller.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtServiceTest {

    @MockBean
    private SecretService secretService;

    @Autowired
    public JwtService jwtService;

    @Before
    public void setUp() throws Exception {

        when(secretService.getHS256SecretBytes()).thenReturn(new byte[] {1,2,3,4,5});

        when(secretService.getSigningKeyResolver()).thenReturn(new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return new byte[] {1,2,3,4,5};
            }
        });

    }

    @Test
    public void checkJwtToken() {

        UserDto userDto = new UserDto("AlexTim", "Alex", "Tim", "AlexTim@ya.ru", "123");
        String jwt = jwtService.createJwt(userDto);

        UserDto parser = jwtService.parser(jwt);
        Assert.assertEquals(userDto, parser);
    }

}
