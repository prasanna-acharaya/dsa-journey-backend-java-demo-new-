package com.bom.dsa.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityContextRepository(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());

            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

                List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities.split(","))
                        .filter(auth -> !auth.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        grantedAuthorities);

                return Mono.just(new SecurityContextImpl(auth));
            }
        }

        return Mono.empty();
    }
}
