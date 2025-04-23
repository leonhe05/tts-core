package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("ali_order")
public class Order {

    @TableId
    private String orderId;

    private String userId;

    private String amount;

    private String subject;

    private String status;

    private Date createTime;

    private Date updateTime;

}
