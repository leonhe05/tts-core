package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameLoginRequest {

    @JsonProperty("code")
    private String code;

}
