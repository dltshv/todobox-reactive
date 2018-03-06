package io.dltshv.todobox.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "todobox.jwt")
@Getter
@Setter
public class JwtProperties {

    String header;
    String param;
    String prefix;
    String secret;
    Long expiration;

}
