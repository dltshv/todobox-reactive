package io.dltshv.todobox.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtSigningProperties signingProperties;

    public Claims parseJwt(String token) {
        return Jwts.parser()
                .setSigningKey(signingProperties.getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

}
