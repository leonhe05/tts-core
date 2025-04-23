package com.leon.application.service;

import com.leon.application.protocol.BaseResponse;
import com.leon.application.protocol.OrderRequest;
import com.leon.application.protocol.OrderResponse;
import com.leon.common.exception.BizException;
import com.leon.domain.aggregate.Order;
import com.leon.domain.aggregate.User;
import com.leon.domain.gateway.AlipayGateway;
import com.leon.domain.repository.OrderRepository;
import com.leon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final AlipayGateway alipayGateway;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final String subjectTemplate = "AI语音合成字数购买-%s字";

    public OrderResponse submitOrder(String userId, String amount) {
        String orderId = System.currentTimeMillis() + userId;
        String subject = String.format(subjectTemplate, getWordsByAmount(amount));
        orderRepository.submitOrder(orderId, userId, amount, subject);

        return OrderResponse.success(orderId, alipayGateway.submitOrder(orderId, amount, subject));
    }

    public BaseResponse queryOrder(OrderRequest request) {
        Order order = orderRepository.getById(request.getOrderId());
        if ("TRADE_SUCCESS".equals(order.getStatus())) {
            User user = userRepository.findByUserId(request.getUserId());
            return OrderResponse.success("订单支付成功", user.getRemainWords());
        }
        String status = alipayGateway.queryOrder(request.getOrderId());
        log.info("当前状态: [{}]", status);
        if ("TRADE_SUCCESS".equals(status)) {
            int result = orderRepository.updateOrder(request.getOrderId(), "TRADE_SUCCESS");
            if (result == 1) {
                userRepository.returnWords(request.getUserId(), getWordsByAmount(order.getAmount()));
                log.info("订单支付成功[{}]，增加字数[{}]", request.getOrderId(), getWordsByAmount(order.getAmount()));
            }
            User user = userRepository.findByUserId(request.getUserId());
            return OrderResponse.success("订单支付成功", user.getRemainWords());
        }

        return OrderResponse.fail("01", "订单未支付");
    }

    public int getWordsByAmount(String amount) {
        if ("0.5".equals(amount)) {
            return 2000;
        }
        if ("2".equals(amount)) {
            return 10000;
        }
        if ("18".equals(amount)) {
            return 100000;
        }
        if ("138".equals(amount)) {
            return 1000000;
        }
        throw new BizException("61", "金额不合法");
    }

}
