package com.leon.domain.repository;

public interface RecordRepository {

    void record(String ip, String content, int length);

    void recordWithoutLimit(String ip, String content, int length);

}
