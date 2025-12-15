package com.bom.dsa.config;

import com.bom.dsa.security.SecurityContextRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
@SecurityScheme(name = SecurityConfig.SECURITY_SCHEME_NAME, scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class SecurityConfig {

        public static final String SECURITY_SCHEME_NAME = "bearerAuth";

        private final SecurityContextRepository securityContextRepository;

        public SecurityConfig(SecurityContextRepository securityContextRepository) {
                this.securityContextRepository = securityContextRepository;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                                                .authenticationEntryPoint((exchange, ex) -> Mono
                                                                .fromRunnable(() -> exchange.getResponse()
                                                                                .setStatusCode(HttpStatus.UNAUTHORIZED)))
                                                .accessDeniedHandler((exchange, denied) -> Mono
                                                                .fromRunnable(() -> exchange.getResponse()
                                                                                .setStatusCode(HttpStatus.FORBIDDEN))))
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .securityContextRepository(securityContextRepository)
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers("/api/v1/auth/**").permitAll()
                                                .pathMatchers("/actuator/**").permitAll()
                                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                                                "/webjars/**", "/swagger-resources/**")
                                                .permitAll()
                                                .anyExchange().authenticated())
                                .build();
        }

        @Bean
        public ReactiveAuthenticationManager reactiveAuthenticationManager(
                        ReactiveUserDetailsService userDetailsService) {
                UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                                userDetailsService);
                authenticationManager.setPasswordEncoder(passwordEncoder());
                return authenticationManager;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(
                                List.of("http://localhost:5173", "https://dsa-loan-management.onrender.com"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
