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
import java.util.*;
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

        // 添加真实用户数据
        for (GameUser user : users) {
            boolean isCurrentUser = currentOpenId != null && currentOpenId.equals(user.getOpenId());
            if (isCurrentUser) {
                currentUserRank = rank;
            }

            GameRankResponse.RankItem item = new GameRankResponse.RankItem();
            item.setRank(rank++);
            item.setOpenId(user.getOpenId());
            item.setNickName(user.getNickName());
            item.setAvatarUrl(user.getAvatarUrl());
            item.setScore(user.getHighScore());
            item.setIsCurrentUser(isCurrentUser);
            rankList.add(item);
        }

        // 添加伪造数据
        List<GameRankResponse.RankItem> mockData = getMockRankData(rank);
        for (GameRankResponse.RankItem mockItem : mockData) {
            if (currentOpenId != null && currentOpenId.equals(mockItem.getOpenId())) {
                currentUserRank = mockItem.getRank();
            }
            rankList.add(mockItem);
        }

        // 按分数降序排序
        rankList.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        // 重新设置排名
        for (int i = 0; i < rankList.size(); i++) {
            rankList.get(i).setRank(i + 1);
        }

        // 更新当前用户排名
        if (currentOpenId != null) {
            for (int i = 0; i < rankList.size(); i++) {
                if (currentOpenId.equals(rankList.get(i).getOpenId())) {
                    currentUserRank = i + 1;
                    rankList.get(i).setIsCurrentUser(true);
                    break;
                }
            }
        }

        // 只保留前50名
        if (rankList.size() > 50) {
            rankList = rankList.subList(0, 50);
        }

        return GameRankResponse.of(rankList, currentUserRank);
    }

    private List<GameRankResponse.RankItem> getMockRankData(int startRank) {
        List<GameRankResponse.RankItem> mockList = new ArrayList<>();

        // 预定义的20条用户信息（从需求文档中提取）
        String[][] mockUsers = {
            {"晴天", "https://wx.qlogo.cn/mmopen/ZuibYmCZ9P5sOAeXJBlj7k0nZSAIJLbiaVNLaVyGsicV6Inia6FibVibXXtwbsAdXoF5ew3RT3jib50t6JHGoYVJIcUCRHLbxaHwVHP/64"},
            {"慈慈", "https://wx.qlogo.cn/mmopen/aOndPibdiaibQvJibCPftJqy0Cqcsbj1Sfoiaialsaz26HZ0jxHF0RVoUy5a5q1iaCSIUwSOtp9viaicmZSn59RYhBnSWav8WTia4rBwrt/64"},
            {"52赫兹", "https://wx.qlogo.cn/mmopen/aOndPibdiaibQtaae7chR0UYhVS7NE8SJmiczSEkPibD5n47qcSakaSiavNdrhrFls2b2XOQhH17q31yob4HPr56NwBu6oymT4QXlx4FR6eem9iaqP6rYYY7StcwTUMJSzT0bxS/64"},
            {"有棵塑料树", "https://wx.qlogo.cn/mmopen/PiajxSqBRaEIv1tvdyCmt0w5oBJcCCh7H9ZlnD79rJUQOdkSjDcKLJ90HNrdw9DxxuU3yBw5HtZbibLksZ5cGOv2uia1m2YO3UbyrSAzn6hqicbBOZHLA4RWOKcSelyPdxm7/64"},
            {"。", "https://wx.qlogo.cn/mmopen/PiajxSqBRaEIk4RFMK45DZolICiaeWibMaagHicUxibibmSLKiaTCicUP3nR6eytuI3232PSPYMhws9Z18wdPwpANjZZ7PfHhqseBFkR8SBF4P6Ys5VEkvnEibo9R9w7Ak5oOLjsD/64"},
            {"小汪ᶘ ᵒᴥᵒᶅ", "https://wx.qlogo.cn/mmopen/mzAuxjL3zJ5I6X980OgTbpZhtjINWfRdMQAMfp5vNYzPb0c5s5JxW4ibfNfW96SINZ8Ydh4sCmmuPbEHM3ZwNgCZs0wgRMicsC/64"},
            {"半岛铁盒七里香🎈", "https://wx.qlogo.cn/mmopen/ZuibYmCZ9P5v21RZTxW0pacH9PBmqyZQo2x9GoQe7DCNYOrYqWnWrLTC52VGvHrpXTfhDQx4uaNLAHtVYIiaGyjLNsbMF9yXzs5WiaI3Z4NPm4uX9yjmlFggKaWsuSrXL8Y/64"},
            {"桑枝੭ ᐕ)੭*⁾⁾", "https://wx.qlogo.cn/mmopen/04LrpEqAf4uKQOz7ibrJbGlr05m9EgdWicDwV78Kml6uJyr20FyoRAzAp7HyVX5G1SyibJTbbOVx3AANdtib1aicEDxgd5OiaMibfC2Jd4teOoqv7PhxBwN4kgehs940bnN04VA/64"},
            {"Rachel＆潘彼得", "https://wx.qlogo.cn/mmopen/aOndPibdiaibQvjdOic6EoyqQL67ia2pe5gfHM5ia8XHibFctbLkaTcr4ZwfGbsI10MicWAmDwG7pTjpdcOKBEh6yQ8TeN69uyZPkSVKRQRsQC46OUhsudxY9PYTtIwpQLhUkNA6/64"},
            {"on", "https://wx.qlogo.cn/mmopen/ecCvKUjKMEZXpn2O0bGOsPcZhTpIx3shFwom6ERzxgy4sNPmoQSqSYjwPLneK5uXHtQCjgAsmdmvogEJ4ScHUZbp60NXsyuw6z26SsLPY9cmpicVWZ9nNuRvwI0U2uVBk/64"},
            {"葳", "https://wx.qlogo.cn/mmopen/ecCvKUjKMEYVuvhhO3mytIFNx9fEKI2jUZ4nd4d9LwsRY3yIIp58qXntSU3Vial4RTj3bibhXLV0y69UhUg6USWz266viauhRHnf60V4ibCfQO6q6aF6qLTWHp1T9uyhic7Io/64"},
            {"Wu～🍏", "https://wx.qlogo.cn/mmopen/aOndPibdiaibQspUl2Uhk3KOmehd44uSE2LvicOAX7V3dCnniaRF0A8XwFQBBpIxL1YicgjOmLibibvObrZACRBGSLrHecaQgN8sibCFaqkibHzG5v1iaeNUzSiaH50DtlSIDblhSp7U/64"},
            {"December", "https://wx.qlogo.cn/mmopen/04LrpEqAf4tNavLNMADZzGULic2DmAAUB7XJsXET6k3NxI796pfunYqpfDS8uWuldate00uXwfxa5riaGveKqQHs7wUUboTgFZ/64"},
            {"多喝开水", "https://wx.qlogo.cn/mmopen/ZuibYmCZ9P5s6BSXeQfbrYWfku9OKTucJqQ0pliczFvclFHoYOZXJll45l3L4zV903uyVkyVP5FDm2MK1lWNxYA5Ij3Qy5H4EK/64"},
            {"彩虹金刚", "https://wx.qlogo.cn/mmopen/ZuibYmCZ9P5uloyxGVuJJJ5mlno52ia2gHbDs8QlAdaib22TZsLQjEBvsue0WgbSGHJpVyv1baIxGJE42QzSqpgvutVldkMcKAWOYhktibnOM5jXWj4CmuhJxtQxVfg99w1t/64"},
            {"伟杰", "https://wx.qlogo.cn/mmopen/ajNVdqHZLLAmpqrZjjPUqBzjmQJcR0ib786UAq0TbT0GPUxxeZic180249BDicaQFsvjliblH0L1V9ppKLgJVngX0UVTPlS8gxD8wQu3icHWAAwqEpiaKWWhyUibes9H7UyvuFs/64"},
            {"安慕希", "https://wx.qlogo.cn/mmopen/04LrpEqAf4tEZ4uZTyj1WnxZFg41UpDxxJa4WpEhlccjbIshEm3ia54vHytv7UmQazbPN8enoKWThL8l3hSPMPc9CmM8p592oCCMOE9TlK9gRkkc755AxD05m9iaVxl9vq/64"},
            {"yaya", "https://wx.qlogo.cn/mmopen/PiajxSqBRaELnNf4o3eEXqaTr1QpzPCEee7Q2mCHIcYlIVzT7Tr9oDh4nicpJ5uRKocskdVIxicHc0G4nXRUnwwNOWuOan6VwRypsrib73CHau8jA16czAUkTL4wSR3KsDlY/64"},
            {"&", "https://wx.qlogo.cn/mmopen/04LrpEqAf4tcSZpTSn5dXt2FSNaN3dJ1OkADCEmS87JVR7V7LcDVdjFiagcTze9smaY3sx7ajNu1VaVBDh63SuEZjtQlOcDdV65yw7dXJ9zsh8s6YsvMAeKYOrp8k9JjS/64"},
            {"豆橛子拌生姜", "https://wx.qlogo.cn/mmopen/ZuibYmCZ9P5uRd5z3vyhUEfQaeEribW9ZmTN18yFVtic6ib8c412T69DMerktRrOwXYtz3bfNibnRrhMtONGyicORGW3Rs4ACpfUjT4rncCBIPuq6mctut3rlicgdzE5OmqoFsF/64"}
        };

        // 分数从110到129
        int score = 110;
        for (int i = 0; i < mockUsers.length && score <= 129; i++) {
            GameRankResponse.RankItem item = new GameRankResponse.RankItem();
            item.setRank(startRank + i);
            item.setOpenId("mock_openid_" + i);
            item.setNickName(mockUsers[i][0]);
            item.setAvatarUrl(mockUsers[i][1]);
            item.setScore(score++);
            item.setIsCurrentUser(false);
            mockList.add(item);
        }

        return mockList;
    }

}
