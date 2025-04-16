package com.leon.domain.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("API_KEY")
public class ApiKey {

    private String key;

    private Long totalWords;

    private Long remainWords;

    private ApiKey() {}

    public ApiKey(String key, Long totalWords, Long remainWords) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty.");
        }
        this.key = key;
        this.totalWords = (totalWords != null && totalWords >= 0) ? totalWords : 0L;
        this.remainWords = (remainWords != null && remainWords >= 0)
                           ? Math.min(remainWords, this.totalWords)
                           : this.totalWords;
    }

    public void decrementWords(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to decrement must be positive.");
        }
        if (this.remainWords < amount) {
            throw new IllegalStateException("Insufficient remaining words for key: " + this.key);
        }
        this.remainWords -= amount;
    }

    public void updateTotalWords(long newTotalWords, boolean adjustRemaining) {
         if (newTotalWords < 0) {
            throw new IllegalArgumentException("Total words cannot be negative.");
        }
        this.totalWords = newTotalWords;
        if (adjustRemaining && this.remainWords > this.totalWords) {
            this.remainWords = this.totalWords;
        }
    }

    public void updateRemainingWords(long newRemainWords) {
        if (newRemainWords < 0) {
            throw new IllegalArgumentException("Remaining words cannot be negative.");
        }
         if (newRemainWords > this.totalWords) {
             throw new IllegalArgumentException("Remaining words cannot exceed total words (" + this.totalWords + ").");
         }
        this.remainWords = newRemainWords;
    }

} 