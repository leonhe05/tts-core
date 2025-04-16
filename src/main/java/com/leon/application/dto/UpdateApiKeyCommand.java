package com.leon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command DTO for updating an existing ApiKey.
 * Key is typically provided via path parameter, not in the body for PUT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApiKeyCommand {

    private Long totalWords;

    private Long remainWords;
}