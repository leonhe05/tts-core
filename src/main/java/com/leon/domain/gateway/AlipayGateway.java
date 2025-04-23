package com.leon.domain.gateway;

import com.alipay.api.AlipayApiException;

public interface AlipayGateway {

    String getAlipayOpenId(String code) throws AlipayApiException;

    String submitOrder(String orderId, String amount, String subject);

    String queryOrder(String orderId);
}
