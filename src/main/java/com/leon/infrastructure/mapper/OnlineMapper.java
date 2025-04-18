package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leon.domain.aggregate.Online;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OnlineMapper extends BaseMapper<Online> {}
