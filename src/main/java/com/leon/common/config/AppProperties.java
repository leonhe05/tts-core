package com.leon.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(value = "leon.biz")
public class AppProperties {

    private String baiduApiKey;

    private String aliApiKey;

    private String cUid = "8asdjkl13789asdf1238dj24k378";

}
