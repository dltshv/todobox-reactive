package io.dltshv.todobox;

import io.dltshv.todobox.jwt.CustomReactiveAuthenticationManager;
import io.dltshv.todobox.jwt.JwtAuthenticationEntryPoint;
import io.dltshv.todobox.service.AuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration {

    private ServerAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                ReactiveAuthenticationManager authenticationManager,
                                                AuthenticationWebFilter apiAuthenticationWebFilter) throws Exception {

        http.httpBasic().disable();
        http.formLogin().disable();
        http.csrf().disable();
        http.logout().disable();

        http.authenticationManager(authenticationManager);
        http.authorizeExchange().pathMatchers("/auth/**").permitAll();
        http.securityContextRepository(securityContextRepository());
        http.authorizeExchange().anyExchange().authenticated();
        http.addFilterAt(apiAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
                                                        PasswordEncoder encoder) {
        return new CustomReactiveAuthenticationManager(userDetailsService, encoder);
    }

    @Bean
    AuthenticationWebFilter apiAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager,
                                                       AuthenticationConverter authenticationConverter) {
        try {
            AuthenticationWebFilter apiAuthenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
            apiAuthenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));
            apiAuthenticationWebFilter.setAuthenticationConverter(authenticationConverter);
            apiAuthenticationWebFilter.setRequiresAuthenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"));

            apiAuthenticationWebFilter.setSecurityContextRepository(securityContextRepository());

            return apiAuthenticationWebFilter;
        } catch (Exception e) {
            throw new BeanInitializationException("Could not initialize AuthenticationWebFilter apiAuthenticationWebFilter.", e);
        }
    }

    @Bean
    public WebSessionServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}