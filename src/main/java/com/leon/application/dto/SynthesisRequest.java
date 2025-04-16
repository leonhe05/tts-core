package com.leon.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynthesisRequest {
    private String text;
    // 可以根据需要添加其他参数，例如语速(spd)、音调(pit)、音量(vol)、发音人(per)等
} 