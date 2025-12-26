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
public class VerifyApprovalResponse {
    private String name;
    private int value; // 1 for approved, 0 for pending
    private LocalDateTime approvedDate;
    private String approverId;
}
