package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameRankResponse extends BaseResponse {

    @JsonProperty("rank_list")
    private List<RankItem> rankList;

    @Data
    public static class RankItem {
        @JsonProperty("rank")
        private Integer rank;

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("nick_name")
        private String nickName;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("score")
        private Integer score;

        @JsonProperty("is_current_user")
        private Boolean isCurrentUser;
    }

    @JsonProperty("current_user_rank")
    private Integer currentUserRank;

    public static GameRankResponse of(List<RankItem> rankList, Integer currentUserRank) {
        GameRankResponse response = new GameRankResponse();
        response.setToSuccess();
        response.setRankList(rankList);
        response.setCurrentUserRank(currentUserRank);
        return response;
    }

}
