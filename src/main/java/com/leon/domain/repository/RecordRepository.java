package com.leon.domain.repository;

public interface RecordRepository {

    void record(String ip, String content, int length);

}
