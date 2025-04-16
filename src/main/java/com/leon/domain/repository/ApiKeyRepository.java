package com.leon.domain.repository;

import com.leon.domain.model.ApiKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ApiKey aggregates.
 * Defines the contract for data access operations.
 */
public interface ApiKeyRepository {

    /**
     * Saves a new ApiKey or updates an existing one.
     * @param apiKey The ApiKey aggregate to save.
     * @return true if saved/updated successfully, false otherwise.
     */
    boolean save(ApiKey apiKey);

    /**
     * Finds an ApiKey by its key.
     * @param key The key of the ApiKey.
     * @return An Optional containing the ApiKey if found, otherwise empty.
     */
    Optional<ApiKey> findById(String key);

    /**
     * Finds all ApiKeys.
     * @return A list of all ApiKeys.
     */
    List<ApiKey> findAll();

    /**
     * Deletes an ApiKey by its key.
     * @param key The key of the ApiKey to delete.
     * @return true if deleted successfully, false otherwise.
     */
    boolean deleteById(String key);

     /**
     * Atomically decrements the remaining words for a given key.
     * This is placed here because the specific implementation might require
     * a more direct/atomic update than loading the aggregate, modifying, and saving.
     *
     * @param key The API key.
     * @param wordsToDecrement The number of words to decrement.
     * @return true if the update was successful (key exists, sufficient words), false otherwise.
     */
    boolean decrementRemainWords(String key, long wordsToDecrement);

} 