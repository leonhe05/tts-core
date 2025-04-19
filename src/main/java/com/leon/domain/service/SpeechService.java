package com.leon.domain.service;

import com.leon.domain.Converter;
import com.leon.domain.aggregate.SpeechContext;
import com.leon.domain.gateway.SpeechGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.leon.common.AudioUtils;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final SpeechGateway speechGateway;

    public byte[] speech(SpeechContext speechContext) throws IOException, UnsupportedAudioFileException {
        speechContext.optimize();

        if (speechContext.getChats().isEmpty()) {
            return new byte[0];
        }

        List<byte[]> audioSegments = new ArrayList<>();
        for (var chat : speechContext.getChats()) {
            try {
                audioSegments.add(
                        speechGateway.speech(Converter.INSTANCE.of(chat, speechContext.getQuality()))
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate speech for a chat segment.", e);
            }
        }

        return AudioUtils.mergeWavByteArrays(audioSegments);
    }

}
