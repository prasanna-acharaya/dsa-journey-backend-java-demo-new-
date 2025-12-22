package com.bom.dsa.controller;

import com.bom.dsa.dto.response.BillingResponse;
import com.bom.dsa.dto.request.LoginRequest;
import com.bom.dsa.dto.response.LoginResponse;
import com.bom.dsa.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthController.class)
@org.springframework.context.annotation.Import(com.bom.dsa.config.TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void login_Success() {
        LoginRequest request = new LoginRequest("testuser", "password");
        LoginResponse response = LoginResponse.builder()
                .accessToken("mock-token")
                .username("testuser")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("mock-token")
                .jsonPath("$.username").isEqualTo("testuser");
    }
}
