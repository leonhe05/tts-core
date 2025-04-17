package com.leon.domain.service;

import com.leon.domain.Converter;
import com.leon.domain.aggregate.SpeechContext;
import com.leon.domain.gateway.SpeechGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final SpeechGateway speechGateway;

    public byte[] speech(SpeechContext speechContext) {
        speechContext.optimize();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        speechContext.getChats().forEach(chat -> {
            try {
                outputStream.write(
                        speechGateway.speech(Converter.INSTANCE.of(chat, speechContext.getAudioSample()))
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return outputStream.toByteArray();
    }

}
