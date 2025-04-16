package com.leon.application.service;

import com.leon.application.dto.ApiKeyDTO;
import com.leon.application.dto.CreateApiKeyCommand;
import com.leon.application.dto.UpdateApiKeyCommand;
import com.leon.domain.model.ApiKey;
import com.leon.domain.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.leon.common.exception.DuplicateResourceException;
import com.leon.common.exception.InsufficientFundsException;
import com.leon.common.exception.InvalidInputException;
import com.leon.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Optional<ApiKeyDTO> findApiKeyByKey(String key) {
        return apiKeyRepository.findById(key)
                .map(this::convertToDTO);
    }

    private ApiKey findApiKeyByKeyOrThrow(String key) {
         return apiKeyRepository.findById(key)
               .orElseThrow(() -> new ResourceNotFoundException("API Key not found with key: " + key));
    }

    public List<ApiKeyDTO> findAllApiKeys() {
        return apiKeyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ApiKeyDTO createApiKey(CreateApiKeyCommand command) {
        ApiKey newApiKey = new ApiKey(
                command.getKey(),
                command.getTotalWords(),
                command.getRemainWords()
        );

        apiKeyRepository.save(newApiKey);
        return convertToDTO(newApiKey);
    }

    @Transactional
    public ApiKeyDTO updateApiKey(String key, UpdateApiKeyCommand command) {
        ApiKey existingApiKey = findApiKeyByKeyOrThrow(key);

        if(command.getTotalWords() != null) {
            existingApiKey.updateTotalWords(command.getTotalWords(), true);
        }
         if(command.getRemainWords() != null) {
             try {
                existingApiKey.updateRemainingWords(command.getRemainWords());
             } catch (IllegalArgumentException e) {
                 throw new InvalidInputException("Invalid remaining words value: " + command.getRemainWords(), e);
             }
         }

        boolean saved = apiKeyRepository.save(existingApiKey); // save handles insert or update

        if (!saved) {
            throw new RuntimeException("Failed to save API Key update for key: " + key);
        }
        return convertToDTO(existingApiKey);
    }

    @Transactional
    public void decrementWords(String key, long wordsToDecrement) {
         if (wordsToDecrement <= 0) {
             throw new InvalidInputException("Words to decrement must be positive.");
         }
         boolean success = apiKeyRepository.decrementRemainWords(key, wordsToDecrement);
         if (!success) {
            if(apiKeyRepository.findById(key).isEmpty()){
                 throw new ResourceNotFoundException("API Key not found: " + key);
            } else {
                 throw new InsufficientFundsException("Failed to decrement words for key " + key + ", likely insufficient remaining words.");
            }
         }

    }

    /**
     * Converts an ApiKey domain object to an ApiKeyDTO.
     */
    private ApiKeyDTO convertToDTO(ApiKey apiKey) {
        if (apiKey == null) {
            return null;
        }
        return new ApiKeyDTO(
                apiKey.getKey(),
                apiKey.getTotalWords(),
                apiKey.getRemainWords()
        );
    }
} 