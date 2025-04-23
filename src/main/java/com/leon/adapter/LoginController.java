package com.leon.adapter;

import com.leon.application.protocol.BaseResponse;
import com.leon.application.protocol.LoginRequest;
import com.leon.application.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public BaseResponse login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest.getCode());
    }

    @PostMapping("/refresh")
    public BaseResponse refresh(@RequestHeader("User-Id") String userId) {
        return loginService.refresh(userId);
    }
} 