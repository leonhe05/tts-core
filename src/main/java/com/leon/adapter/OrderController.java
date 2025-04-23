package com.leon.adapter;

import com.leon.application.protocol.BaseResponse;
import com.leon.application.protocol.OrderRequest;
import com.leon.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/submitOrder")
    public BaseResponse submitOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.getUserId(), orderRequest.getAmount());
    }

    @PostMapping("/queryOrder")
    public BaseResponse login(@RequestBody OrderRequest orderRequest) {
        return orderService.queryOrder(orderRequest);
    }

}
