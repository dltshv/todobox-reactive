package io.dltshv.todobox;

import io.dltshv.todobox.jwt.JwtService;
import io.dltshv.todobox.jwt.JwtSigningProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public JwtSigningProperties jwtSigningProperties() {
        return new JwtSigningProperties();
    }

    @Bean
    public JwtService jwtService(JwtSigningProperties jwtSigningProperties) {
        return new JwtService(jwtSigningProperties);
    }

}
