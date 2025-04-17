package com.leon.adapter;

import com.leon.application.protocol.SynthesisRequest;
import com.leon.application.service.SynthesisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SpeechController {

    private final SynthesisService synthesisService;

    @PostMapping(value = "/synthesize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> synthesize(@RequestBody SynthesisRequest synthesisRequest) throws IOException {
        byte[] audioData = synthesisService.synthesize(synthesisRequest);
        return new ResponseEntity<>(audioData, withHeaders(), HttpStatus.OK);
    }

    private HttpHeaders withHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/wav"));
        headers.set("Content-Disposition", "attachment; filename=\"output.wav\"");
        return headers;
    }
} 