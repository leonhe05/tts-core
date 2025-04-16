package com.leon.controller;

import com.leon.application.dto.ApiKeyDTO;
import com.leon.application.dto.CreateApiKeyCommand;
import com.leon.application.dto.UpdateApiKeyCommand;
import com.leon.application.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/all")
    public ResponseEntity<List<ApiKeyDTO>> getAllApiKeys() {
        List<ApiKeyDTO> apiKeys = apiKeyService.findAllApiKeys();
        return ResponseEntity.ok(apiKeys);
    }

    @PostMapping("/{key}/get")
    public ResponseEntity<ApiKeyDTO> getApiKeyByKey(@PathVariable String key) {
        return apiKeyService.findApiKeyByKey(key)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.leon.common.exception.ResourceNotFoundException("API Key not found: " + key));
    }

    @PostMapping
    public ResponseEntity<ApiKeyDTO> createApiKey(@RequestBody CreateApiKeyCommand command) {
        ApiKeyDTO createdApiKey = apiKeyService.createApiKey(command);
        URI location = URI.create(String.format("/api/keys/%s", createdApiKey.getKey()));
        return ResponseEntity.created(location).body(createdApiKey);
    }

    @PostMapping("/{key}/update")
    public ResponseEntity<ApiKeyDTO> updateApiKey(@PathVariable String key, @RequestBody UpdateApiKeyCommand command) {
        ApiKeyDTO updatedApiKey = apiKeyService.updateApiKey(key, command);
        return ResponseEntity.ok(updatedApiKey);
    }

    @PostMapping("/{key}/decrement-words")
    public ResponseEntity<Void> decrementWords(@PathVariable String key, @RequestParam long wordsToDecrement) {
        apiKeyService.decrementWords(key, wordsToDecrement);
        return ResponseEntity.ok().build();
    }

} 