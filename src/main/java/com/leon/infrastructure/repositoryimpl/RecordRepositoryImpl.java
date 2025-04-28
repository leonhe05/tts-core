package com.leon.infrastructure.repositoryimpl;

import com.leon.common.BizAssert;
import com.leon.domain.aggregate.Record;
import com.leon.domain.repository.RecordRepository;
import com.leon.infrastructure.mapper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@RequiredArgsConstructor
public class RecordRepositoryImpl implements RecordRepository {

    private final RecordMapper recordMapper;

    @Override
    public void record(String ip, String content, int length) {
        int words = recordMapper.getTodayWords(ip);
        BizAssert.isTrue(words + length <= 300, "20", "未登录当日限额300字，已用[{}]字，本次[{}]字，超过限额"
            , words, length);

        recordMapper.insert(Record.builder()
                        .ip(ip)
                        .content(content)
                        .length(length)
                        .createTime(new Date())
                        .build());
    }
}
