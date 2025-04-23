package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leon.domain.aggregate.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
