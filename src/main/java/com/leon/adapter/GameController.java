package com.leon.adapter;

import com.leon.application.protocol.*;
import com.leon.application.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/login")
    public BaseResponse login(@RequestBody GameLoginRequest request) {
        return gameService.login(request);
    }

    @PostMapping("/user/save")
    public BaseResponse saveUserInfo(@RequestBody GameSaveUserRequest request) {
        return gameService.saveUserInfo(request);
    }

    @PostMapping("/user/info")
    public BaseResponse getUserInfo(@RequestBody GameOpenIdRequest request) {
        return gameService.getUserInfo(request);
    }

    @PostMapping("/coin/add")
    public BaseResponse addCoin(@RequestBody GameOpenIdRequest request) {
        return gameService.addCoin(request);
    }

    @PostMapping("/skin/buy")
    public BaseResponse buySkin(@RequestBody GameBuySkinRequest request) {
        return gameService.buySkin(request);
    }

    @PostMapping("/rank/monthly")
    public BaseResponse getMonthlyRank(@RequestBody GameRankRequest request) {
        return gameService.getMonthlyRank(request);
    }

    @PostMapping("/rank/weekly")
    public BaseResponse getWeeklyRank(@RequestBody GameRankRequest request) {
        return gameService.getWeeklyRank(request);
    }

    @PostMapping("/rank/all")
    public BaseResponse getAllTimeRank(@RequestBody GameRankRequest request) {
        return gameService.getAllTimeRank(request);
    }

}
