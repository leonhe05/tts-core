package com.leon.infrastructure.repositoryimpl;

import com.leon.common.BizAssert;
import com.leon.domain.aggregate.User;
import com.leon.domain.repository.UserRepository;
import com.leon.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public void consume(String userId, int consumeWords) {
        User user = userMapper.selectById(userId);
        BizAssert.isNotNull(user, "20", "该用户不存在");

        long remain = user.getRemainWords() - consumeWords;
        BizAssert.isTrue(remain >= 0, "21", "可用字数不足，此次转换消耗字数[{}]，可用字数[{}]", consumeWords,
                user.getRemainWords());

        user.setRemainWords(remain);
        int result = userMapper.updateById(user);
        BizAssert.isTrue(result > 0, "22", "扣减字数异常，请重试");
    }

    @Override
    public void returnWords(String userId, int consumeWords) {
        User user = userMapper.selectById(userId);
        BizAssert.isNotNull(user, "20", "该用户不存在");

        long remain = user.getRemainWords() + consumeWords;

        user.setRemainWords(remain);
        userMapper.updateById(user);
    }

    @Override
    public User findByOpenId(String openId) {
        return userMapper.selectByOpenId(openId);
    }

    @Override
    public User findByUserId(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public User saveOrUpdateByOpenId(User user) {
        User existingUser = findByOpenId(user.getOpenId());
        if (existingUser != null) {
            return existingUser;
        } else {
            userMapper.insert(user);
            return findByOpenId(user.getOpenId());
        }
    }
}
