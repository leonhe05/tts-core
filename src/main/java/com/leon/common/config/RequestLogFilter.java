package com.leon.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RequestLogFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.put("trace_id", UUID.randomUUID().toString().replace("-", ""));
        ServletHttpLogRequest logRequest = new ServletHttpLogRequest(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        log.info("接收到{}请求 =>  {} {}", method, uri, logRequest.getBodyAsString());
        chain.doFilter(logRequest, cachingResponse);

        int status = response.getStatus();
        byte[] responseContent = cachingResponse.getContentAsByteArray();

        log.info("响应 <= {} {}", status, new String(responseContent));
        MDC.remove("trace_id");
        cachingResponse.copyBodyToResponse();
    }

}