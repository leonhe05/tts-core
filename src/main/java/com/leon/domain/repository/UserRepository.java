package com.leon.domain.repository;

public interface UserRepository {

    void consume(String userId, int consumeWords);

    void returnWords(String userId, int consumeWords);

} 