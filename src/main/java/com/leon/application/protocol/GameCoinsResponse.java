package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameCoinsResponse extends BaseResponse {

    @JsonProperty("coins")
    private Integer coins;

    public static GameCoinsResponse of(Integer coins) {
        GameCoinsResponse response = new GameCoinsResponse();
        response.setToSuccess();
        response.setCoins(coins);
        return response;
    }

}
