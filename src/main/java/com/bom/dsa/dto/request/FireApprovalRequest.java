package com.bom.dsa.dto.request;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class FireApprovalRequest {
    private String flowId;
    private String deadline;
    private String reminderInDays;
    private Map<String, Object> data;
}
