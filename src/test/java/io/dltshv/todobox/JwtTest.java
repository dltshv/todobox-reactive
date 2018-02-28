package io.dltshv.todobox;

import io.dltshv.todobox.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JwtTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void jwtTestEncodeDecode() {

        String token = createJwt();
        assertThat(token).isNotBlank();

        System.out.println(token);

        Claims jwt = jwtService.parseJwt(token);

        assertThat(jwt).isNotNull();
        assertThat(jwt.get("username", String.class)).isEqualTo("John");
        assertThat(jwt.get("roles", String.class)).isEqualTo("USER");

    }

    public String createJwt() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("my-secret");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        DefaultClaims claims = new DefaultClaims();
        claims.put("username", "John");
        claims.put("roles", "USER");

        return Jwts.builder()
                .setClaims(claims)
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }

    public Claims parseJwt(String token) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("my-secret");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
    }

}
