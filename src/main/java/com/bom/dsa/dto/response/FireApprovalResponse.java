package com.bom.dsa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FireApprovalResponse {
    private String runningFlowId;
    private List<List<ApprovalDetail>> approvalDetails;
    private boolean finalStatus;

    @Data
    public static class ApprovalDetail {
        private String name;
        private String status;
        private String comment;
        private String timestamp;
    }
}
