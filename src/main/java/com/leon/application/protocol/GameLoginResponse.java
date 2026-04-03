package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameLoginResponse extends BaseResponse {

    @JsonProperty("open_id")
    private String openId;

    public static GameLoginResponse of(String openId) {
        GameLoginResponse response = new GameLoginResponse();
        response.setToSuccess();
        response.setOpenId(openId);
        return response;
    }

}
