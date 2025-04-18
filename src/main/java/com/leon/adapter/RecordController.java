package com.leon.adapter;

import com.leon.application.protocol.BaseResponse;
import com.leon.domain.gateway.RecordGateway;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecordController {

    private final RecordGateway recordGateway;

    @PostMapping("/online")
    public BaseResponse online(
            @RequestHeader(value = "X-Real-IP", required = false) String ip,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Origin", required = false) String origin) {

        recordGateway.online(ip, userAgent, origin);
        return BaseResponse.success();
    }
}
