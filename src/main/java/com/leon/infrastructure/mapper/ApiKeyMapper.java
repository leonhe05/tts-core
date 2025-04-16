package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// Import the domain model instead of the old entity
import com.leon.domain.model.ApiKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis Mapper Interface for ApiKey.
 * Located in the infrastructure layer.
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
    // BaseMapper provides standard CRUD.
    // Custom database operations specific to ApiKey can be added here.
} 