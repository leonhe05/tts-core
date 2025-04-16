package com.leon.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynthesisRequest {

    private String text;

    private String person;

    private String speed;

    private String pitch;

    private String volume;

    private Integer audioSample;
}