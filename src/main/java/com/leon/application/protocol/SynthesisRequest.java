package com.leon.application.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynthesisRequest {

    private List<Chat> chats;

    private Integer audioSample;

    private String userId;

    @Data
    public static class Chat {

        private String text;

        private String person;

        private String speed;

        private String pitch;

        private String volume;
    }
}