package com.leon.domain.aggregate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("game_user")
public class GameUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openId;

    private String nickName;

    private String avatarUrl;

    private Integer highScore;

    private LocalDateTime scoreTime;

    private Integer coins;

    private String ownedSkins;

    private String currentSkin;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
