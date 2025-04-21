package com.leon.application.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class BaseResponse {

    @JsonProperty("ret_code")
    private String retCode;

    @JsonProperty("ret_msg")
    private String retMsg;

    public static BaseResponse success() {
        return new BaseResponse("00", "success");
    }

    public void setToSuccess() {
        retCode = "00";
        retMsg = "success";
    }

    public static BaseResponse fail(String code, String msg) {
        return new BaseResponse(code, msg);
    }
}
