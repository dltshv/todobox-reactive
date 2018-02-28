package io.dltshv.todobox.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

@Component
public class JwtSigningProperties {

//    @Value("${}")
    private String secret = "my-secret";
    @Getter
    private SignatureAlgorithm signatureAlgorithm;
    @Getter
    private byte[] apiKeySecretBytes;
    @Getter
    private Key signingKey;

    @PostConstruct
    public void init() {
        signatureAlgorithm = SignatureAlgorithm.HS256;
        apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

}
