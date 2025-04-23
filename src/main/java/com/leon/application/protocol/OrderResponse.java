package com.leon.application.protocol;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderResponse extends BaseResponse {

    private Long remainWords;

    private String orderMsg;

    private String orderId;

    public static OrderResponse success(String orderMsg) {
        OrderResponse response = OrderResponse.builder()
                .orderMsg(orderMsg)
                .build();
        response.setToSuccess();
        return response;
    }

    public static OrderResponse success(String orderId, String orderMsg) {
        OrderResponse response = OrderResponse.builder()
                .orderMsg(orderMsg)
                .orderId(orderId)
                .build();
        response.setToSuccess();
        return response;
    }

    public static OrderResponse success(String orderMsg, Long remainWords) {
        OrderResponse response = success(orderMsg);
        response.setRemainWords(remainWords);
        return response;
    }

}
