package com.leon.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter; // Use Getter instead of Data for better control

/**
 * Represents an API Key aggregate root.
 * It manages its own lifecycle and invariants.
 */
@Getter // Provide getters for fields
@TableName("API_KEY") // Keep persistence annotations for simplicity with MP
public class ApiKey {

    @TableId(value = "KEY", type = IdType.INPUT)
    private String key;

    @TableField("TOTAL_WORDS")
    private Long totalWords;

    @TableField("REMAIN_WORDS")
    private Long remainWords;

    // Private constructor for persistence frameworks
    private ApiKey() {}

    // Factory method or public constructor for creation
    public ApiKey(String key, Long totalWords, Long remainWords) {
        // Add validation/invariant checks here if needed
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty.");
        }
        this.key = key;
        this.totalWords = (totalWords != null && totalWords >= 0) ? totalWords : 0L;
        // Ensure remainWords does not exceed totalWords upon creation
        this.remainWords = (remainWords != null && remainWords >= 0)
                           ? Math.min(remainWords, this.totalWords)
                           : this.totalWords; // Default remaining to total if not specified or invalid
    }

    /**
     * Decrements the remaining word count.
     * Ensures that the remaining words do not go below zero.
     *
     * @param amount The number of words to decrement. Must be positive.
     * @throws IllegalArgumentException if amount is not positive.
     * @throws IllegalStateException if there are insufficient remaining words.
     */
    public void decrementWords(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to decrement must be positive.");
        }
        if (this.remainWords < amount) {
            throw new IllegalStateException("Insufficient remaining words for key: " + this.key);
        }
        this.remainWords -= amount;
    }

    /**
     * Updates the total word count.
     * Can optionally adjust remaining words to match the new total if they exceed it.
     * @param newTotalWords The new total words count. Must be non-negative.
     * @param adjustRemaining If true, sets remaining words to the new total if current remaining exceeds it.
     */
    public void updateTotalWords(long newTotalWords, boolean adjustRemaining) {
         if (newTotalWords < 0) {
            throw new IllegalArgumentException("Total words cannot be negative.");
        }
        this.totalWords = newTotalWords;
        if (adjustRemaining && this.remainWords > this.totalWords) {
            this.remainWords = this.totalWords;
        }
        // Consider if remainWords should be allowed to be > totalWords after update?
        // Current logic adjusts down if requested.
    }

     /**
     * Updates the remaining word count directly.
     * Ensures remaining words are not negative and do not exceed total words.
     * Use with caution, prefer decrementWords for usage tracking.
     * @param newRemainWords The new remaining words count.
     */
    public void updateRemainingWords(long newRemainWords) {
        if (newRemainWords < 0) {
            throw new IllegalArgumentException("Remaining words cannot be negative.");
        }
         if (newRemainWords > this.totalWords) {
             throw new IllegalArgumentException("Remaining words cannot exceed total words (" + this.totalWords + ").");
         }
        this.remainWords = newRemainWords;
    }

    // Note: Explicit setters are avoided to enforce state changes through methods.
    // Equals and HashCode should be based on the identity (key) if needed.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKey apiKey = (ApiKey) o;
        return java.util.Objects.equals(key, apiKey.key);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(key);
    }
} 