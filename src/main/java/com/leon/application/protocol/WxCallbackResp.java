package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxCallbackResp {

    @JsonProperty("ErrCode")
    private Integer errCode;

    @JsonProperty("ErrMsg")
    private String msg;

    public static WxCallbackResp success() {
        return new WxCallbackResp(0, "success");
    }
}
