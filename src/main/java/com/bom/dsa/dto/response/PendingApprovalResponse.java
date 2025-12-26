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
public class PendingApprovalResponse {
    private String id;
    private String dsaId;
    private String dsaName;
    private String dsaUniqueCode;
    private String userId;
    private String productType;
    private LocalDateTime approvedAt;
}
