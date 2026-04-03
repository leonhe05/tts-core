package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameOpenIdRequest {

    @JsonProperty("open_id")
    private String openId;

}
