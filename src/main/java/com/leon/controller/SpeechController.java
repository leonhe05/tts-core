package com.leon.controller;

import com.leon.application.dto.SynthesisRequest;
import com.leon.application.service.SpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
@Slf4j
public class SpeechController {

    private final SpeechService speechService;

    @PostMapping(value = "/synthesize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody SynthesisRequest synthesisRequest) {
        log.info("Received speech synthesis request for text: {}", synthesisRequest.getText());
        try {
            byte[] audioData = speechService.synthesizeSpeech(synthesisRequest.getText());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            // Suggest filename to browser if downloaded
            headers.setContentDispositionFormData("attachment", "synthesis.wav");
            headers.setContentLength(audioData.length);

            log.info("Successfully synthesized speech for text: {}", synthesisRequest.getText());
            return new ResponseEntity<>(audioData, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Rely on GlobalExceptionHandler for specific exceptions thrown by the service
            // But log the error here as well
            log.error("Error synthesizing speech for text: {}", synthesisRequest.getText(), e);
            // Re-throw or let GlobalExceptionHandler handle it based on service exceptions
            // For now, assume service throws exceptions handled globally
            // If service returns null or specific error codes, handle here
            throw new RuntimeException("Speech synthesis failed: " + e.getMessage(), e); // Or return a generic error response
        }
    }
} 