package com.bom.dsa.service;

import com.bom.dsa.dto.request.LoginRequest;
import com.bom.dsa.dto.response.LoginResponse;
import com.bom.dsa.entity.User;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.UserRepository;
import com.bom.dsa.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider, authenticationManager);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("testDsa", "password");
        User user = new User();
        user.setDsaUniqueCode("testDsa");
        user.setIsLocked(false);
        user.setPassword("encodedPassword");

        Authentication authentication = new UsernamePasswordAuthenticationToken("testDsa", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        when(userRepository.findByDsaUniqueCode("testDsa")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("validToken");
        when(jwtTokenProvider.getExpirationTime()).thenReturn(3600L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        Mono<LoginResponse> result = authService.login(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getAccessToken().equals("validToken"))
                .verifyComplete();
    }

    @Test
    void login_Failure_UserNotFound() {
        LoginRequest request = new LoginRequest("unknownUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(new UsernamePasswordAuthenticationToken("unknownUser", "password")));
        when(userRepository.findByDsaUniqueCode("unknownUser")).thenReturn(Optional.empty());

        Mono<LoginResponse> result = authService.login(request);

        StepVerifier.create(result)
                .expectError(CustomExceptions.ResourceNotFoundException.class)
                .verify();
    }
}
