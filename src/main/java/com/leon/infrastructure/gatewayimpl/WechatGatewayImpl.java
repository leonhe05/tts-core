package com.leon.infrastructure.gatewayimpl;

import com.leon.common.JsonUtils;
import com.leon.domain.gateway.WechatGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class WechatGatewayImpl implements WechatGateway {

    @Value("${wechat.miniapp.appid:}")
    private String appId;

    @Value("${wechat.miniapp.secret:}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getOpenId(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, secret, code);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = JsonUtils.parseObject(response.getBody(), Map.class);

            if (result == null || result.containsKey("errcode")) {
                log.error("获取微信openId失败: {}", result);
                throw new RuntimeException("获取微信用户信息失败: " + result);
            }

            return (String) result.get("openid");
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new RuntimeException("获取微信用户信息失败");
        }
    }

}
