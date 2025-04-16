package com.leon.application.service;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.leon.application.dto.SynthesisRequest;
import com.leon.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.FormBody;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechService {

    private static final String TEXT_2_AUDIO_URL = "https://tsn.baidu.com/text2audio";
    private final OkHttpClient httpClient;
    private final AppProperties appProperties;

    public byte[] synthesize(SynthesisRequest synthesisRequest) throws IOException {
        FormBody formBody = getFormBody(synthesisRequest);

        Request request = new Request.Builder()
                .url(TEXT_2_AUDIO_URL)
                .header("Authorization", "Bearer " + appProperties.getBaiduApiKey())
                .post(formBody)
                .build();

        Response response = this.httpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        Assert.isTrue(responseBody != null && response.isSuccessful(), "合成失败");

        String contentType = response.header("Content-Type");
        Assert.notNull(contentType, "合成失败");

        if (contentType.startsWith("audio/")) {
            return responseBody.bytes();
        }
        log.error("合成失败: [{}", responseBody.string());
        return null;
    }

    private FormBody getFormBody(SynthesisRequest synthesisRequest) {
        return new FormBody.Builder(StandardCharsets.UTF_8)
                .add("tex", synthesisRequest.getText())
                .add("cuid", appProperties.getCUid())
                .add("ctp", "1")
                .add("lan", "zh")
                .add("spd", synthesisRequest.getSpeed())
                .add("pit", synthesisRequest.getPitch())
                .add("vol", synthesisRequest.getVolume())
                .add("per", synthesisRequest.getPerson())
                .add("aue", "6")
                .add("audio_ctrl", "{\"sampling_rate\":%d}".formatted(synthesisRequest.getAudioSample()))
                .build();
    }

} 