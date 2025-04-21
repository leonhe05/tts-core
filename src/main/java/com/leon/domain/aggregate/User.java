package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private String userId;

    private String openId;

    private Long totalWords;

    private Long remainWords;

} 