package com.leon.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.leon.domain.model.ApiKey;
import com.leon.domain.repository.ApiKeyRepository;
import com.leon.infrastructure.mapper.ApiKeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Keep transactional for atomic operations

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of the ApiKeyRepository interface.
 */
@Repository // Mark as a Spring repository component
public class ApiKeyRepositoryImpl implements ApiKeyRepository {

    private final ApiKeyMapper apiKeyMapper;

    @Autowired
    public ApiKeyRepositoryImpl(ApiKeyMapper apiKeyMapper) {
        this.apiKeyMapper = apiKeyMapper;
    }

    /**
     * Saves (inserts or updates) an ApiKey.
     * MyBatis-Plus BaseMapper's saveOrUpdate logic can be complex.
     * Here we explicitly check existence first for clarity.
     */
    @Override
    @Transactional
    public boolean save(ApiKey apiKey) {
        if (apiKey == null) {
            return false;
        }
        // Check if exists
        ApiKey existing = apiKeyMapper.selectById(apiKey.getKey());
        int result;
        if (existing != null) {
            // Update existing
            result = apiKeyMapper.updateById(apiKey);
        } else {
            // Insert new
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

    /**
     * Implements the atomic decrement using MyBatis-Plus UpdateWrapper.
     * Replicates the logic from the old ServiceImpl.
     */
    @Override
    @Transactional // Ensure atomicity of the check-and-update
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
