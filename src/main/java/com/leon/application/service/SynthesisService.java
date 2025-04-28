package com.leon.application.service;

import com.alipay.service.schema.util.StringUtil;
import com.leon.application.Converter;
import com.leon.application.protocol.SynthesisRequest;
import com.leon.domain.aggregate.SpeechContext;
import com.leon.domain.repository.RecordRepository;
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
    private final RecordRepository recordRepository;

    public byte[] synthesize(SynthesisRequest synthesisRequest, String userId, String ip) throws UnsupportedAudioFileException, IOException, ExecutionException, InterruptedException {
        SpeechContext speechContext = Converter.INSTANCE.of(synthesisRequest);

        int consume = speechContext.getConsumeWords();
        if (consume == 0) {
            return new byte[]{};
        }
        if (StringUtil.isEmpty(userId)) {
            recordRepository.record(ip, speechContext.getDigest(), consume);
        } else {
            userRepository.consume(userId, consume);
        }

        try {
            return speechService.speech(speechContext);
        } catch (Exception e) {
            if (!StringUtil.isEmpty(userId)) {
                log.info("合成失败，ID[{}]，返还额度[{}]", userId, speechContext.getConsumeWords());
                userRepository.returnWords(userId, speechContext.getConsumeWords());
            }
            throw e;
        }
    }

} 