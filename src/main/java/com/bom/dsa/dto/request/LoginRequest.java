package com.bom.dsa.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NonNull   // (va = "Username is required")
    private String username;

    @NonNull // (message = "Password is required")
    private String password;


}
