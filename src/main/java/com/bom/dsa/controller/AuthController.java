package com.bom.dsa.controller;

import com.bom.dsa.dto.request.LoginRequest;
import com.bom.dsa.dto.response.LoginResponse;
import com.bom.dsa.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get JWT token")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        return authService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh JWT token")
    public Mono<ResponseEntity<LoginResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        // Extract username from current token and generate new one
        // This is a simplified implementation
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user (client should discard token)")
    public Mono<ResponseEntity<Void>> logout() {
        // JWT tokens are stateless, so logout is handled client-side
        return Mono.just(ResponseEntity.ok().build());
    }
}
