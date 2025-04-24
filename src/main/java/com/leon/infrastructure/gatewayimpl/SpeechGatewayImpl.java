package com.leon.infrastructure.gatewayimpl;

import com.leon.common.BizAssert;
import com.leon.common.JsonUtils;
import com.leon.common.config.AppProperties;
import com.leon.domain.dto.SpeechDTO;
import com.leon.domain.gateway.SpeechGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechGatewayImpl implements SpeechGateway {

    private static final String TEXT_2_AUDIO_URL = "https://tsn.baidu.com/text2audio";
    private final OkHttpClient httpClient;
    private final AppProperties appProperties;

    @Override
    @SuppressWarnings("all")
    public byte[] speech(SpeechDTO speechDTO) throws IOException {
        FormBody formBody = getFormBody(speechDTO);

        Request request = new Request.Builder()
                .url(TEXT_2_AUDIO_URL)
                .header("Authorization", "Bearer " + appProperties.getBaiduApiKey().stream().findAny().get())
                .post(formBody)
                .build();
        log.info("向百度发起语音合成请求: [{}]", JsonUtils.toString(speechDTO));

        Response response = this.httpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        BizAssert.isTrue(responseBody != null && response.isSuccessful(), "11", "合成失败");

        String contentType = response.header("Content-Type");
        BizAssert.isNotBlank(contentType, "11", "合成失败");
        BizAssert.isTrue(contentType.startsWith("audio/"), "11", "合成失败");

        return responseBody.bytes();
    }

    private FormBody getFormBody(SpeechDTO speechDTO) {
        return new FormBody.Builder(StandardCharsets.UTF_8)
                .add("tex", speechDTO.getText())
                .add("cuid", appProperties.getCUid())
                .add("ctp", "1")
                .add("lan", "zh")
                .add("spd", speechDTO.getSpeed())
                .add("pit", speechDTO.getPitch())
                .add("vol", speechDTO.getVolume())
                .add("per", speechDTO.getPerson())
                .add("aue", "6")
                .add("audio_ctrl", "{\"sampling_rate\":24000}")
                .build();
    }
}
