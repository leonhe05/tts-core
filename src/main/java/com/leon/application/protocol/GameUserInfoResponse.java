package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameUserInfoResponse extends BaseResponse {

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("nick_name")
    private String nickName;

    @JsonProperty("high_score")
    private Integer highScore;

    @JsonProperty("coins")
    private Integer coins;

    @JsonProperty("owned_skins")
    private List<String> ownedSkins;

    @JsonProperty("current_skin")
    private String currentSkin;

    public static GameUserInfoResponse of(String openId, String nickName, Integer highScore,
                                          Integer coins, List<String> ownedSkins, String currentSkin) {
        GameUserInfoResponse response = new GameUserInfoResponse();
        response.setToSuccess();
        response.setOpenId(openId);
        response.setNickName(nickName);
        response.setHighScore(highScore);
        response.setCoins(coins);
        response.setOwnedSkins(ownedSkins);
        response.setCurrentSkin(currentSkin);
        return response;
    }

}
