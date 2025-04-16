package com.leon.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.leon.domain.model.ApiKey;
import com.leon.domain.repository.ApiKeyRepository;
import com.leon.infrastructure.mapper.ApiKeyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Keep transactional for atomic operations

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApiKeyRepositoryImpl implements ApiKeyRepository {

    private final ApiKeyMapper apiKeyMapper;

    @Override
    @Transactional
    public boolean save(ApiKey apiKey) {
        if (apiKey == null) {
            return false;
        }
        ApiKey existing = apiKeyMapper.selectById(apiKey.getKey());
        int result;
        if (existing != null) {
            result = apiKeyMapper.updateById(apiKey);
        } else {
            result = apiKeyMapper.insert(apiKey);
        }
        return result > 0;
    }

    @Override
    public Optional<ApiKey> findById(String key) {
        return Optional.ofNullable(apiKeyMapper.selectById(key));
    }

    @Override
    public List<ApiKey> findAll() {
        return apiKeyMapper.selectList(null); // null query wrapper means select all
    }

    @Override
    @Transactional
    public boolean deleteById(String key) {
        return apiKeyMapper.deleteById(key) > 0;
    }

    @Override
    @Transactional
    public boolean decrementRemainWords(String key, long wordsToDecrement) {
         if (key == null || wordsToDecrement <= 0) {
            return false;
        }
        UpdateWrapper<ApiKey> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("KEY", key)
                     .ge("REMAIN_WORDS", wordsToDecrement) // Check condition
                     .setSql("REMAIN_WORDS = REMAIN_WORDS - " + wordsToDecrement); // Atomic update

        int updatedRows = apiKeyMapper.update(null, updateWrapper); // Pass null entity, use wrapper criteria

        return updatedRows > 0;
    }
}
