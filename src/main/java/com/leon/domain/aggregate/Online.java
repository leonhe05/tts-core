package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("online")
public class Online {

    private String ip;

    private String userAgent;

    private String origin;

    private Date time;

}
