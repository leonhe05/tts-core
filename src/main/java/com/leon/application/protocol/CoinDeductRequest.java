package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoinDeductRequest {

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("coin_num")
    private Integer coinNum;

}
