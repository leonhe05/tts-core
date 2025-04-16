package com.leon.controller;

import com.leon.application.dto.ChatRequest;
import com.leon.application.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    // Use a dedicated thread pool for SSE tasks
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequest chatRequest) {
        log.info("Received SSE stream request for prompt: {}", chatRequest.getPrompt());
        // Timeout set to a large value (e.g., 1 hour) or rely on client/server heartbeat
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        sseExecutor.execute(() -> {
            try {
                chatService.streamChatCompletion(chatRequest.getPrompt(), emitter);
            } catch (Exception e) {
                log.error("Error during SSE streaming for prompt: {}", chatRequest.getPrompt(), e);
                emitter.completeWithError(e);
            }
            // Note: emitter.complete() should be called by the service when the stream finishes normally
        });

        log.info("SSE emitter returned for prompt: {}", chatRequest.getPrompt());
        return emitter;
    }
} 