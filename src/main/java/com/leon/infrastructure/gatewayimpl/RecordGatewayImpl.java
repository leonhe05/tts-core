package com.leon.infrastructure.gatewayimpl;

import com.leon.domain.aggregate.Online;
import com.leon.domain.gateway.RecordGateway;
import com.leon.infrastructure.mapper.OnlineMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@RequiredArgsConstructor
public class RecordGatewayImpl implements RecordGateway {

    private final OnlineMapper onlineMapper;

    @Override
    public void online(String ip, String userAgent, String origin) {
        onlineMapper.insert(Online.builder()
                        .ip(ip)
                        .origin(origin)
                        .userAgent(userAgent)
                        .time(new Date())
                        .build());
    }
}
