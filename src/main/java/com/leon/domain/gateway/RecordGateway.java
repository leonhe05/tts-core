package com.leon.domain.gateway;

import jakarta.servlet.http.HttpServletRequest;

public interface RecordGateway {

    void online(HttpServletRequest req, String userAgent, String origin);

}
