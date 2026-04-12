package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WechatMessage {

    @JsonProperty("ToUserName")
    private String toUserName;

    @JsonProperty("FromUserName")
    private String fromUserName;

    @JsonProperty("CreateTime")
    private Long createTime;

    @JsonProperty("MsgType")
    private String msgType;

    @JsonProperty("Event")
    private String event;

    @JsonProperty("debug_str")
    private String debugStr;
}
