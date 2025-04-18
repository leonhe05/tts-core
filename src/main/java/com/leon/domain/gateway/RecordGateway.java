package com.leon.domain.gateway;

import jakarta.servlet.http.HttpServletRequest;

public interface RecordGateway {

    void online(String ip, String userAgent, String origin);

}
