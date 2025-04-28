package com.leon.application.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String code;

    private String userId;

    private String password;
} 