-- 微信小游戏用户表
CREATE TABLE IF NOT EXISTS `game_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `open_id` VARCHAR(64) NOT NULL COMMENT '微信用户唯一标识',
    `nick_name` VARCHAR(128) DEFAULT NULL COMMENT '用户昵称',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '用户头像URL',
    `high_score` INT NOT NULL DEFAULT 0 COMMENT '历史最高分',
    `score_time` DATETIME DEFAULT NULL COMMENT '最高分产生时间',
    `coins` INT NOT NULL DEFAULT 0 COMMENT '当前金币数量',
    `owned_skins` VARCHAR(512) DEFAULT 'default' COMMENT '已拥有的皮肤，逗号分隔',
    `current_skin` VARCHAR(64) DEFAULT 'default' COMMENT '当前使用的皮肤',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_high_score` (`high_score`),
    KEY `idx_score_time` (`score_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信小游戏用户表';
