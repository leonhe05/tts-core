package com.leon.application.service;

import com.leon.application.dto.ApiKeyDTO;
import com.leon.application.dto.CreateApiKeyCommand;
import com.leon.application.dto.UpdateApiKeyCommand;
import com.leon.domain.model.ApiKey;
import com.leon.domain.repository.ApiKeyRepository;
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

/**
 * Application Service for managing ApiKey use cases.
 * Orchestrates domain logic and interacts with repositories.
 */
@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    // --- Query Methods ---

    public Optional<ApiKeyDTO> findApiKeyByKey(String key) {
        return apiKeyRepository.findById(key)
                .map(this::convertToDTO); // Convert domain object to DTO
    }

    // findApiKeyByKey that throws exception if not found - useful for commands
    private ApiKey findApiKeyByKeyOrThrow(String key) {
         return apiKeyRepository.findById(key)
               .orElseThrow(() -> new ResourceNotFoundException("API Key not found with key: " + key));
    }

    public List<ApiKeyDTO> findAllApiKeys() {
        return apiKeyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- Command Methods ---

    @Transactional // Use cases modifying data should be transactional
    public ApiKeyDTO createApiKey(CreateApiKeyCommand command) {
        // Input validation
        if (command == null || command.getKey() == null || command.getKey().isEmpty()) {
            // throw new IllegalArgumentException("API key must be provided for creation.");
            throw new InvalidInputException("API key must be provided for creation.");
        }
        // Check for duplicates
        if (apiKeyRepository.findById(command.getKey()).isPresent()) {
             // throw new IllegalArgumentException("API key already exists: " + command.getKey());
             throw new DuplicateResourceException("API key already exists: " + command.getKey());
        }

        // Use factory method/constructor of the domain object
        ApiKey newApiKey = new ApiKey(
                command.getKey(),
                command.getTotalWords(),
                command.getRemainWords()
        );

        apiKeyRepository.save(newApiKey);
        return convertToDTO(newApiKey);
    }

    @Transactional
    // public Optional<ApiKeyDTO> updateApiKey(String key, UpdateApiKeyCommand command) {
    public ApiKeyDTO updateApiKey(String key, UpdateApiKeyCommand command) {
        if (key == null || command == null) {
             // throw new IllegalArgumentException("Key and command must not be null for update.");
             throw new InvalidInputException("Key and command must not be null for update.");
        }

        // Find existing or throw ResourceNotFoundException
        ApiKey existingApiKey = findApiKeyByKeyOrThrow(key);

        // Apply updates using domain methods
        // We assume full update here (PUT semantics). Partial updates (PATCH) would need different logic.
        // For simplicity, directly updating fields or using dedicated update methods.
        // A more robust approach might involve specific update methods on the domain object.
        // For now, let's re-create the object state based on the command for a full update.
        // Or better, add update methods to the domain object.
        // Using the update methods added to ApiKey:
        if(command.getTotalWords() != null) {
            // Decide if remaining should be adjusted when total changes (e.g., true)
            existingApiKey.updateTotalWords(command.getTotalWords(), true);
        }
         if(command.getRemainWords() != null) {
            // Directly setting remaining words - use carefully
             try {
                existingApiKey.updateRemainingWords(command.getRemainWords());
             } catch (IllegalArgumentException e) {
                 throw new InvalidInputException("Invalid remaining words value: " + command.getRemainWords(), e);
             }
         }

        boolean saved = apiKeyRepository.save(existingApiKey); // save handles insert or update

        // If save fails, repository should ideally throw an exception
        if (!saved) {
            // Consider a more specific exception, e.g., PersistenceException
            throw new RuntimeException("Failed to save API Key update for key: " + key);
        }
        return convertToDTO(existingApiKey);
    }

    @Transactional
    // public boolean decrementWords(String key, long wordsToDecrement) {
    public void decrementWords(String key, long wordsToDecrement) {
         if (wordsToDecrement <= 0) {
             // throw new IllegalArgumentException("Words to decrement must be positive.");
             throw new InvalidInputException("Words to decrement must be positive.");
         }
         // Option 1: Use direct atomic update in repository (already implemented)
         boolean success = apiKeyRepository.decrementRemainWords(key, wordsToDecrement);
         if (!success) {
            // Optionally check if the key exists to differentiate between 'not found' and 'insufficient funds'
            // Re-check existence *after* the failed decrement attempt
            if(apiKeyRepository.findById(key).isEmpty()){
                 // throw new RuntimeException("API Key not found: " + key); // Or a custom NotFoundException
                 throw new ResourceNotFoundException("API Key not found: " + key);
            } else {
                 // throw new IllegalStateException("Failed to decrement words for key " + key + ", likely insufficient remaining words.");
                 throw new InsufficientFundsException("Failed to decrement words for key " + key + ", likely insufficient remaining words.");
            }
         }
         // return true; // Indicate success -> changed to void return type

        // Option 2: Load aggregate, call domain method, save aggregate
        /*
        // Find or throw not found exception first
        ApiKey apiKey = findApiKeyByKeyOrThrow(key);
        try {
            apiKey.decrementWords(wordsToDecrement); // Use domain logic
            apiKeyRepository.save(apiKey); // Persist changes
            // return true; // Change to void
        } catch (IllegalStateException e) {
             // Handle insufficient words specifically
             // Log or rethrow as needed
             // throw new IllegalStateException("Insufficient remaining words for key: " + key, e);
             throw new InsufficientFundsException("Insufficient remaining words for key: " + key, e);
        } catch (IllegalArgumentException e) { // Catch invalid decrement amount from domain
            throw new InvalidInputException(e.getMessage(), e);
        }
        catch (Exception e) {
             // Handle other potential errors during save
             throw new RuntimeException("Failed to decrement words for key: " + key, e);
        }
        */
    }

    @Transactional
    // public boolean deleteApiKey(String key) {
    public void deleteApiKey(String key) {
        if (key == null || key.isEmpty()) {
             throw new InvalidInputException("Key must be provided for deletion.");
        }
        ApiKey apiKey = findApiKeyByKeyOrThrow(key); // Check existence first
        // if (!apiKeyRepository.findById(key).isPresent()) {
        //    return false; // Indicate not found -> now handled by findApiKeyByKeyOrThrow
        // }
        boolean deleted = apiKeyRepository.deleteById(apiKey.getKey()); // Use the key from the retrieved object
        if (!deleted) {
             // If deleteById returns false unexpectedly (e.g., concurrency issue)
             throw new RuntimeException("Failed to delete API Key with key: " + key);
        }
        // Return type changed to void
    }

    // --- Helper Methods ---

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