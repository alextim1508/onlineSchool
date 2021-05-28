package com.alextim.service.gateway;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SecretServiceTest {

    private SecretService secretService;

    @Before
    public void setUp() throws Exception {
        secretService = new SecretService();
        secretService.refreshSecrets();
    }

    @Test
    public void getHS256SecretBytesTest() {
        SigningKeyResolver signingKeyResolver = secretService.getSigningKeyResolver();

        JwsHeader header = new DefaultJwsHeader();
        header.setAlgorithm(SignatureAlgorithm.HS256.getValue());
        Claims claims = new DefaultClaims();

        byte[] bytes = ((SigningKeyResolverAdapter) signingKeyResolver).resolveSigningKeyBytes(header,claims);

        byte[] hs256SecretBytes = secretService.getHS256SecretBytes();

        Assert.assertArrayEquals(bytes, hs256SecretBytes);
    }

    @Test
    public void getHS384SecretBytesTest() {
        SigningKeyResolver signingKeyResolver = secretService.getSigningKeyResolver();

        JwsHeader header = new DefaultJwsHeader();
        header.setAlgorithm(SignatureAlgorithm.HS384.getValue());
        Claims claims = new DefaultClaims();

        byte[] bytes = ((SigningKeyResolverAdapter) signingKeyResolver).resolveSigningKeyBytes(header,claims);

        byte[] hs384SecretBytes = secretService.getHS384SecretBytes();
        Assert.assertArrayEquals(bytes, hs384SecretBytes);
    }

    @Test
    public void getHS512SecretBytes() {
        SigningKeyResolver signingKeyResolver = secretService.getSigningKeyResolver();

        JwsHeader header = new DefaultJwsHeader();
        header.setAlgorithm(SignatureAlgorithm.HS512.getValue());
        Claims claims = new DefaultClaims();

        byte[] bytes = ((SigningKeyResolverAdapter) signingKeyResolver).resolveSigningKeyBytes(header,claims);

        byte[] hs512SecretBytes = secretService.getHS512SecretBytes();
        Assert.assertArrayEquals(bytes, hs512SecretBytes);
    }

}
