package com.leon.application.service;

import com.leon.application.protocol.*;
import com.leon.application.protocol.CoinDeductRequest;
import com.leon.common.BizAssert;
import com.leon.domain.aggregate.GameUser;
import com.leon.domain.gateway.WechatGateway;
import com.leon.domain.repository.GameUserRepository;
import com.leon.infrastructure.mapper.GameUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final WechatGateway wechatGateway;
    private final GameUserRepository gameUserRepository;
    private final GameUserMapper gameUserMapper;

    public GameLoginResponse login(GameLoginRequest request) {
        BizAssert.isNotBlank(request.getCode(), "10", "登录凭证不能为空");
        String openId = wechatGateway.getOpenId(request.getCode());
        log.info("微信登录成功, openId: {}", openId);

        GameUser user = gameUserRepository.findByOpenId(openId);
        if (user == null) {
            GameUser newUser = GameUser.builder()
                    .openId(openId)
                    .nickName("")
                    .highScore(0)
                    .coins(0)
                    .ownedSkins("default")
                    .currentSkin("default")
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .loginTime(LocalDateTime.now())
                    .build();
            gameUserRepository.save(newUser);
        } else {
            user.setLoginTime(LocalDateTime.now());
            gameUserRepository.update(user);
        }

        return GameLoginResponse.of(openId);
    }

    @Transactional
    public BaseResponse saveUserInfo(GameSaveUserRequest request) {
        BizAssert.isNotBlank(request.getOpenId(), "11", "openId不能为空");

        GameUser user = gameUserRepository.findByOpenId(request.getOpenId());
        if (user == null) {
            user = GameUser.builder()
                    .openId(request.getOpenId())
                    .nickName(request.getNickName())
                    .avatarUrl(request.getAvatarUrl())
                    .highScore(request.getScore() != null ? request.getScore() : 0)
                    .scoreTime(request.getScore() != null && request.getScore() > 0 ? LocalDateTime.now() : null)
                    .coins(Boolean.TRUE.equals(request.getAddCoin()) ? 1 : 0)
                    .ownedSkins("default")
                    .currentSkin("default")
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            gameUserMapper.insert(user);
        } else {
            if (request.getNickName() != null) {
                user.setNickName(request.getNickName());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            if (request.getSkin() != null) {
                user.setCurrentSkin(request.getSkin());
            }
            if (request.getScore() != null && request.getScore() > user.getHighScore()) {
                user.setHighScore(request.getScore());
                user.setScoreTime(LocalDateTime.now());
            }
            if (Boolean.TRUE.equals(request.getAddCoin())) {
                user.setCoins(user.getCoins() + 1);
            }
            gameUserMapper.updateById(user);
        }

        return BaseResponse.success();
    }

    public GameUserInfoResponse getUserInfo(GameOpenIdRequest request) {
        BizAssert.isNotBlank(request.getOpenId(), "11", "openId不能为空");

        GameUser user = gameUserRepository.findByOpenId(request.getOpenId());
        BizAssert.isNotNull(user, "12", "用户不存在");

        List<String> skins = Arrays.asList(user.getOwnedSkins().split(","));

        return GameUserInfoResponse.of(
                user.getOpenId(),
                user.getNickName(),
                user.getHighScore(),
                user.getCoins(),
                skins,
                user.getCurrentSkin()
        );
    }

    @Transactional
    public GameCoinsResponse addCoin(GameOpenIdRequest request) {
        BizAssert.isNotBlank(request.getOpenId(), "11", "openId不能为空");

        boolean success = gameUserRepository.addCoin(request.getOpenId());
        BizAssert.isTrue(success, "13", "添加金币失败");

        GameUser user = gameUserRepository.findByOpenId(request.getOpenId());
        return GameCoinsResponse.of(user.getCoins());
    }

    @Transactional
    public GameCoinsResponse deductCoin(CoinDeductRequest request) {
        BizAssert.isNotBlank(request.getOpenId(), "11", "openId不能为空");
        BizAssert.isTrue(request.getCoinNum() != null && request.getCoinNum() > 0, "19", "扣减金币数量无效");

        GameUser user = gameUserRepository.findByOpenId(request.getOpenId());
        BizAssert.isNotNull(user, "12", "用户不存在");

        BizAssert.isTrue(user.getCoins() >= request.getCoinNum(), "17", "金币不足");

        boolean success = gameUserRepository.deductCoins(request.getOpenId(), request.getCoinNum());
        BizAssert.isTrue(success, "18", "扣除金币失败");

        GameUser updatedUser = gameUserRepository.findByOpenId(request.getOpenId());
        return GameCoinsResponse.of(updatedUser.getCoins());
    }

    @Transactional
    public GameCoinsResponse buySkin(GameBuySkinRequest request) {
        BizAssert.isNotBlank(request.getOpenId(), "11", "openId不能为空");
        BizAssert.isNotBlank(request.getSkin(), "14", "皮肤不能为空");
        BizAssert.isTrue(request.getPrice() != null && request.getPrice() > 0, "15", "价格无效");

        GameUser user = gameUserRepository.findByOpenId(request.getOpenId());
        BizAssert.isNotNull(user, "12", "用户不存在");

        List<String> ownedSkins = Arrays.asList(user.getOwnedSkins().split(","));
        BizAssert.isTrue(!ownedSkins.contains(request.getSkin()), "16", "已拥有该皮肤");

        BizAssert.isTrue(user.getCoins() >= request.getPrice(), "17", "金币不足");

        boolean success = gameUserRepository.deductCoins(request.getOpenId(), request.getPrice());
        BizAssert.isTrue(success, "18", "扣除金币失败");

        String newSkins = user.getOwnedSkins() + "," + request.getSkin();
        gameUserRepository.updateOwnedSkins(request.getOpenId(), newSkins);

        GameUser updatedUser = gameUserRepository.findByOpenId(request.getOpenId());
        return GameCoinsResponse.of(updatedUser.getCoins());
    }

    public GameRankResponse getMonthlyRank(GameRankRequest request) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startTime = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        List<GameUser> users = gameUserMapper.selectMonthlyTop50(startTime, endTime);
        return buildRankResponse(users, request.getOpenId());
    }

    public GameRankResponse getWeeklyRank(GameRankRequest request) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        int week = today.get(weekFields.weekOfWeekBasedYear());
        int year = today.get(weekFields.weekBasedYear());

        LocalDate startOfWeek = LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 1);
        LocalDateTime startTime = startOfWeek.atStartOfDay();
        LocalDateTime endTime = startOfWeek.plusWeeks(1).atStartOfDay();

        List<GameUser> users = gameUserMapper.selectWeeklyTop50(startTime, endTime);
        return buildRankResponse(users, request.getOpenId());
    }

    public GameRankResponse getAllTimeRank(GameRankRequest request) {
        List<GameUser> users = gameUserMapper.selectAllTimeTop50();
        return buildRankResponse(users, request.getOpenId());
    }

    private GameRankResponse buildRankResponse(List<GameUser> users, String currentOpenId) {
        List<GameRankResponse.RankItem> rankList = new ArrayList<>();
        int rank = 1;
        Integer currentUserRank = null;

        for (GameUser user : users) {
            boolean isCurrentUser = currentOpenId != null && currentOpenId.equals(user.getOpenId());
            if (isCurrentUser) {
                currentUserRank = rank;
            }

            GameRankResponse.RankItem item = new GameRankResponse.RankItem();
            item.setRank(rank++);
            item.setOpenId(user.getOpenId());
            item.setNickName(user.getNickName());
            item.setScore(user.getHighScore());
            item.setIsCurrentUser(isCurrentUser);
            rankList.add(item);
        }

        return GameRankResponse.of(rankList, currentUserRank);
    }

}
