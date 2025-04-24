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
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final SpeechGateway speechGateway;
    private final static ExecutorService executor = Executors.newFixedThreadPool(30);

    public byte[] speech(SpeechContext speechContext) throws IOException, UnsupportedAudioFileException, ExecutionException, InterruptedException {
        speechContext.optimize();

        if (speechContext.getChats().isEmpty()) {
            return new byte[0];
        }

        Semaphore  semaphore = new Semaphore(2);

        List<byte[]> audioSegments = new ArrayList<>();
        List<Future<byte[]>> futures = new ArrayList<>();
        for (var chat : speechContext.getChats()) {
            futures.add(executor.submit(() -> {
                try {
                    semaphore.acquire();
                    return speechGateway.speech(Converter.INSTANCE.of(chat, speechContext.getQuality()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }));
        }

        for (Future<byte[]> future : futures) {
            audioSegments.add(future.get());
        }

        return AudioUtils.mergeWavByteArrays(audioSegments);
    }

}
