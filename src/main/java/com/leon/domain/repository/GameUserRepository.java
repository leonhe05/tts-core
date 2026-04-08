package com.leon.domain.repository;

import com.leon.domain.aggregate.GameUser;

public interface GameUserRepository {

    GameUser findByOpenId(String openId);

    void save(GameUser gameUser);

    void update(GameUser gameUser);

    boolean updateScore(String openId, int score);

    boolean addCoin(String openId);

    boolean deductCoins(String openId, int price);

    boolean updateOwnedSkins(String openId, String ownedSkins);

}
