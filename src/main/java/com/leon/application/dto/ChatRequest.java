package com.leon.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String prompt;
    // 可以根据需要添加其他参数，例如模型名称、历史记录等
} 