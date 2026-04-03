package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameSaveUserRequest {

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("nick_name")
    private String nickName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("skin")
    private String skin;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("add_coin")
    private Boolean addCoin;

}
