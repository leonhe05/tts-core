package com.leon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command DTO for creating a new ApiKey.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApiKeyCommand {

    private String key;

    private Long totalWords;

    private Long remainWords;
}