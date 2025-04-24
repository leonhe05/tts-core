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
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynthesisService {

    private final SpeechService speechService;
    private final UserRepository userRepository;

    public byte[] synthesize(SynthesisRequest synthesisRequest, String userId) throws UnsupportedAudioFileException, IOException, ExecutionException, InterruptedException {
        SpeechContext speechContext = Converter.INSTANCE.of(synthesisRequest);

        int consume = speechContext.getConsumeWords();
        if (consume == 0) {
            return new byte[]{};
        }
        userRepository.consume(userId, consume);

        try {
            return speechService.speech(speechContext);
        } catch (Exception e) {
            log.info("合成失败，ID[{}]，返还额度[{}]", userId, speechContext.getConsumeWords());
            userRepository.returnWords(userId, speechContext.getConsumeWords());
            throw e;
        }
    }



} 