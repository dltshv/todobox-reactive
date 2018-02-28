package io.dltshv.todobox;

import io.dltshv.todobox.jwt.JwtAuthentication;
import io.dltshv.todobox.jwt.JwtPrincipal;
import io.dltshv.todobox.jwt.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authenticationManager,
                                                         ServerSecurityContextRepository contextRepository) {
        return http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(contextRepository)
                .authorizeExchange().pathMatchers("/auth/**").permitAll()
                .and()
                .authorizeExchange().pathMatchers("/signup/**").permitAll()
                .and()
                .authorizeExchange().anyExchange().authenticated()
                .and()
                .build();
    }

    @Bean
    public WebFilterChainProxy webFilterChainProxy(SecurityWebFilterChain securityWebFilterChain) {
        return new WebFilterChainProxy(securityWebFilterChain);
    }

    @Component
    static class TodoReactiveAuthenticationManager implements ReactiveAuthenticationManager {

        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
//            if (authentication instanceof JwtAuthentication) {
//                authentication.setAuthenticated(true);
//            }
//            return Mono.just(authentication);

            return Mono.just(
                    new AnonymousAuthenticationToken(
                            "authenticated-user",
                            authentication.getPrincipal(),
                            authentication.getAuthorities())
            );
        }
    }

    @Component
    @RequiredArgsConstructor
    static class TodoServerSecurityContextRepository implements ServerSecurityContextRepository {

        private final JwtService jwtService;

        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            return Mono.just(exchange)
                    .map(e -> e.getRequest().getHeaders().getFirst("Authorization"))
                    .map(jwtService::parseJwt)
//                    .map(this::fromClaimsToAuthentication)
                    .map(this::fromClaimsToUserDetails)
                    .map(this::fromUserDetailsToAuthentication)
                    .map(this::createSecurityContext)
                    .onErrorMap(UnsupportedJwtException.class, ex -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported token"))
                    .onErrorMap(MalformedJwtException.class, ex -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed token"))
                    .onErrorMap(SignatureException.class, ex -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad signature"))
                    .onErrorMap(ExpiredJwtException.class, ex -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired"));
        }

        private SecurityContext createSecurityContext(Authentication authentication) {
            return new SecurityContextImpl(authentication);
        }

        private Authentication fromClaimsToAuthentication(Claims claims) {
            String username = claims.get("username", String.class);
            String roles = claims.get("roles", String.class);
            return new JwtAuthentication(new JwtPrincipal(username), Collections.singletonList(new SimpleGrantedAuthority(roles)));
        }

        private UserDetails fromClaimsToUserDetails(Claims claims) {
            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return AuthorityUtils.createAuthorityList(claims.get("roles", String.class));
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public String getUsername() {
                    return claims.get("username", String.class);
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
        }

        private Authentication fromUserDetailsToAuthentication(UserDetails user) {
            return new AnonymousAuthenticationToken("authenticated-user", user, user.getAuthorities());
        }
    }


}