package com.bom.dsa.client;

import com.bom.dsa.dto.request.*;
import com.bom.dsa.dto.response.*;
import lombok.Data;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ApprovalClient {

    private final WebClient webClient;

    public ApprovalClient(WebClient.Builder webClientBuilder,
            @Value("${services.approval.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<FireApprovalResponse> fireApprovalFlow(FireApprovalRequest request) {
        log.info("Firing approval flow for data: {}", request.getData());

        return webClient.post()
                .uri("/api/v1/approval/run")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FireApprovalResponse.class)
                .doOnError(error -> log.error("Failed to fire approval flow: {}", error.getMessage()));
    }

    public Mono<String> ping() {
        log.info("Pinging approval service...");
        return webClient.get()
                .uri("/api/dsa/approval/ping")
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(s -> log.info("Approval service ping successful: {}", s))
                .onErrorResume(e -> {
                    log.warn("Approval service ping unavailable: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<String> stageApprovals(StageApprovalRequest request) {
        log.info("Staging approvals for DSA: {}", request.getDsaId());
        return webClient.post()
                .uri("/api/dsa/approval/stage")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<AuthorizeApprovalResponse> authorizeApproval(AuthorizeApprovalRequest request) {
        log.info("Authorizing approval for product: {}", request.getProductType());
        return webClient.post()
                .uri("/api/dsa/approval/authorize")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuthorizeApprovalResponse.class);
    }

    public Mono<List<VerifyApprovalResponse>> verifyApprovals(String dsaId) {
        return webClient.post()
                .uri("/api/dsa/approval/verify")
                .bodyValue(java.util.Map.of("dsaId", dsaId))
                .retrieve()
                .bodyToFlux(VerifyApprovalResponse.class)
                .collectList();
    }

    public Mono<List<RawApprovalResponse>> getPendingApprovals(String userId) {
        return webClient.get()
                .uri("/api/dsa/approval/pending/{userId}", userId)
                .retrieve()
                .bodyToFlux(RawApprovalResponse.class)
                .collectList();
    }

    @Data
    public static class RawApprovalResponse {
        private String id;
        private String dsaId;
        private String userId;
        private String productType;
        private String approvedAt; // Use String to avoid Jackson issues if format differs
        private String runningFlowId;
    }
}
