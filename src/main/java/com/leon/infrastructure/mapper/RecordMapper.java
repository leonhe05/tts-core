package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leon.domain.aggregate.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    int getTodayWords(String ip);

}
