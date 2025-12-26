package com.bom.dsa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeApprovalResponse {
    private String status;
    private String targetId;
    private LocalDateTime createdAt;
}
