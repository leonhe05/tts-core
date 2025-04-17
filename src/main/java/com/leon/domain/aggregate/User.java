package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("User")
public class User {

    @TableId
    private String userId;

    private Long totalWords;

    private Long remainWords;

} 