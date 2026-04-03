package com.leon.infrastructure.repositoryimpl;

import com.leon.domain.aggregate.GameUser;
import com.leon.domain.repository.GameUserRepository;
import com.leon.infrastructure.mapper.GameUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameUserRepositoryImpl implements GameUserRepository {

    private final GameUserMapper gameUserMapper;

    @Override
    public GameUser findByOpenId(String openId) {
        return gameUserMapper.selectByOpenId(openId);
    }

    @Override
    public GameUser saveOrUpdate(GameUser gameUser) {
        GameUser existing = gameUserMapper.selectByOpenId(gameUser.getOpenId());
        if (existing == null) {
            gameUserMapper.insert(gameUser);
            return gameUser;
        } else {
            if (gameUser.getNickName() != null) {
                existing.setNickName(gameUser.getNickName());
            }
            if (gameUser.getAvatarUrl() != null) {
                existing.setAvatarUrl(gameUser.getAvatarUrl());
            }
            gameUserMapper.updateById(existing);
            return existing;
        }
    }

    @Override
    public boolean updateScore(String openId, int score) {
        GameUser user = gameUserMapper.selectByOpenId(openId);
        if (user == null) {
            return false;
        }
        if (score > user.getHighScore()) {
            user.setHighScore(score);
            gameUserMapper.updateById(user);
        }
        return true;
    }

    @Override
    public boolean addCoin(String openId) {
        return gameUserMapper.addCoin(openId) > 0;
    }

    @Override
    public boolean deductCoins(String openId, int price) {
        return gameUserMapper.deductCoins(openId, price) > 0;
    }

    @Override
    public boolean updateOwnedSkins(String openId, String ownedSkins) {
        GameUser user = gameUserMapper.selectByOpenId(openId);
        if (user == null) {
            return false;
        }
        user.setOwnedSkins(ownedSkins);
        gameUserMapper.updateById(user);
        return true;
    }

}
