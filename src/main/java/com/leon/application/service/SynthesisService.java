package com.leon.application.service;

import com.leon.application.Converter;
import com.leon.application.protocol.SynthesisRequest;
import com.leon.domain.aggregate.SpeechContext;
import com.leon.domain.repository.UserRepository;
import com.leon.domain.service.SpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynthesisService {

    private final SpeechService speechService;
    private final UserRepository userRepository;

    public byte[] synthesize(SynthesisRequest synthesisRequest) throws UnsupportedAudioFileException, IOException {
        SpeechContext speechContext = Converter.INSTANCE.of(synthesisRequest);

        int consume = speechContext.getConsumeWords();
        if (consume == 0) {
            return new byte[]{};
        }
        userRepository.consume(synthesisRequest.getUserId(), consume);

        try {
            return speechService.speech(speechContext);
        } catch (Exception e) {
            log.info("合成失败，ID[{}]，返还额度[{}]", synthesisRequest.getUserId(), speechContext.getConsumeWords());
            userRepository.returnWords(synthesisRequest.getUserId(), speechContext.getConsumeWords());
            throw e;
        }
    }



} 