package com.bom.dsa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeApprovalRequest {
    private String dsaId;
    private String productType;
    private String userId;
}
