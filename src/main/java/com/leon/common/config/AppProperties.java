package com.leon.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Collections;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(value = "leon.biz")
public class AppProperties {

    private List<String> baiduApiKey;

    private String aliApiKey;

    private String cUid = "8asdjkl13789asdf1238dj24k378";

    public String getRandomBaiduApiKey() {
        Collections.shuffle(baiduApiKey);
        return baiduApiKey.get(0);
    }
}
