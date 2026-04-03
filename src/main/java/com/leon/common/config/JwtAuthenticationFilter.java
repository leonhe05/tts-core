package com.leon.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.application.protocol.BaseResponse;
import com.leon.common.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final ObjectMapper objectMapper;
    private final Set<String> excludedPaths = Set.of("/login", "/actuator/health", "/online", "/normal-login", "/register", "/game");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if (excludedPaths.contains(path) || path.startsWith("/game")) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(token) && path.equals("/synthesize")) {
            chain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(token)) {
            log.warn("Missing JWT token for path: {}", path);
            sendUnauthorizedError(httpResponse, "登录失效，请重新登录");
            return;
        }

        String userId = JwtUtils.getUserIdFromToken(token);

        if (!StringUtils.hasText(userId)) {
            log.warn("Invalid or expired JWT token for path: {}", path);
            sendUnauthorizedError(httpResponse, "登录失效，请重新登录");
            return;
        }

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            private final Map<String, String> customHeaders = Map.of("User-Id", userId);

            @Override
            public String getHeader(String name) {
                return customHeaders.getOrDefault(name, super.getHeader(name));
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                Set<String> headerNames = new HashSet<>();
                Enumeration<String> originalHeaderNames = super.getHeaderNames();
                while (originalHeaderNames.hasMoreElements()) {
                    headerNames.add(originalHeaderNames.nextElement());
                }
                headerNames.addAll(customHeaders.keySet());
                return Collections.enumeration(headerNames);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                List<String> headers = Collections.list(super.getHeaders(name));
                if (customHeaders.containsKey(name)) {
                    headers.add(customHeaders.get(name));
                }
                return Collections.enumeration(headers);
            }
        };

        chain.doFilter(requestWrapper, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        BaseResponse errorResponse = BaseResponse.fail("41", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}