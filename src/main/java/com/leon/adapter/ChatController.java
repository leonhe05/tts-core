package com.leon.adapter;

import com.leon.application.protocol.ChatRequest;
import com.leon.application.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequest chatRequest) {
        log.info("Received SSE stream request for prompt: {}", chatRequest.getPrompt());
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        sseExecutor.execute(() -> {
            try {
                chatService.streamChatCompletion(chatRequest.getPrompt(), emitter);
            } catch (Exception e) {
                log.error("Error during SSE streaming for prompt: {}", chatRequest.getPrompt(), e);
                emitter.completeWithError(e);
            }
        });

        log.info("SSE emitter returned for prompt: {}", chatRequest.getPrompt());
        return emitter;
    }
} 