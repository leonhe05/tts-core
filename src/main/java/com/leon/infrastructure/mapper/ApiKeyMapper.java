package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leon.domain.model.ApiKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {}