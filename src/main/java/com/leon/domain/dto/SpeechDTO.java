package com.leon.domain.dto;

import lombok.Data;

@Data
public class SpeechDTO {

    private String text;

    private String person;

    private String speed;

    private String pitch;

    private String volume;

    private Integer quality;
}
