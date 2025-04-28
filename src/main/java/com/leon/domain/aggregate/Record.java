package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("record")
public class Record {

    private String ip;

    private String content;

    private int length;

    private Date createTime;

}
