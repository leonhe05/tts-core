package com.leon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing ApiKey data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDTO {

    private String key;

    private Long totalWords;

    private Long remainWords;
}