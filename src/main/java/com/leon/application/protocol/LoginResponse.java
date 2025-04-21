package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends BaseResponse{

    @JsonProperty("user_id")
    private String userId;

    private String token;

    @JsonProperty("remain_words")
    private Long remainWords;

    public static LoginResponse of(String token, String userId, Long remainWords) {
        LoginResponse response = LoginResponse.builder()
                .userId(userId)
                .token(token)
                .remainWords(remainWords)
                .build();
        response.setToSuccess();
        return response;
    }
}