package com.leon.domain.gateway;

import com.alipay.api.AlipayApiException;

public interface AlipayLoginGateway {

    String getAlipayOpenId(String code) throws AlipayApiException;
}
