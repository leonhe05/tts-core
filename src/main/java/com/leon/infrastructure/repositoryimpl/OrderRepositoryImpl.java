package com.leon.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.leon.domain.aggregate.Order;
import com.leon.domain.repository.OrderRepository;
import com.leon.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public void submitOrder(String orderId, String userId, String amount, String subject) {
        Order order = Order.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .subject(subject)
                .status("WAIT_BUYER_PAY")
                .createTime(new Date())
                .build();

        orderMapper.insert(order);
    }

    @Override
    public int updateOrder(String orderId, String status) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<Order>()
                .eq(Order::getOrderId, orderId)
                .ne(Order::getStatus, "TRADE_SUCCESS")
                .set(Order::getStatus, status);
        return orderMapper.update(wrapper);
    }

    @Override
    public Order getById(String orderId) {
        return orderMapper.selectById(orderId);
    }
}
