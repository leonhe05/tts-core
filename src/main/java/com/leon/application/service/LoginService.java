package com.leon.application.service;

import com.leon.application.protocol.LoginResponse;
import com.leon.common.BizAssert;
import com.leon.common.JwtUtils;
import com.leon.domain.aggregate.User;
import com.leon.domain.gateway.AlipayGateway;
import com.leon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AlipayGateway alipayGateway;
    private final UserRepository userRepository;
    private static final long INITIAL_REMAIN_WORDS = 2000L;
    private static final long INITIAL_TOTAL_WORDS = 2000L;

    public LoginResponse login(String code) {
        BizAssert.isNotBlank(code, "10", "登录凭证不能为空");

        String openId;
        try {
            openId = alipayGateway.getAlipayOpenId(code);
            log.info("Retrieved openId: {}", openId);
        } catch (Exception e) {
            throw new RuntimeException("获取支付宝用户信息失败");
        }

        User userToSave = User.builder()
                .openId(openId)
                .remainWords(INITIAL_REMAIN_WORDS)
                .totalWords(INITIAL_TOTAL_WORDS)
                .build();

        User user = userRepository.saveOrUpdateByOpenId(userToSave);
        String token = JwtUtils.generateToken(user.getUserId());

        return LoginResponse.of(token, user.getUserId(), user.getRemainWords());
    }
} 