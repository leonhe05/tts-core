package com.leon.domain.repository;

import com.leon.domain.aggregate.Order;

public interface OrderRepository {

    void submitOrder(String orderId, String userId, String amount, String subject);

    int updateOrder(String orderId, String status);

    Order getById(String orderId);
}
