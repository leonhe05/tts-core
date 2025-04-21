package com.leon.domain.repository;

import com.leon.domain.aggregate.User;

public interface UserRepository {

    void consume(String userId, int consumeWords);

    void returnWords(String userId, int consumeWords);

    User findByOpenId(String openId);

    User saveOrUpdateByOpenId(User user);

} 