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

    @PostMapping("/coin/deduct")
    public BaseResponse deductCoin(@RequestBody CoinDeductRequest request) {
        return gameService.deductCoin(request);
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

    /**
     * 微信服务器验证接口（GET请求）
     * 用于配置消息推送URL时的验证
     */
    @GetMapping("/wechat/callback")
    public String verifyWechatServer(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        log.info("收到微信服务器验证请求: signature={}, timestamp={}, nonce={}, echostr={}",
                signature, timestamp, nonce, echostr);
        return echostr;
    }

    /**
     * 接收微信消息推送（POST请求）
     */
    @PostMapping("/wechat/callback")
    public WxCallbackResp receiveWechatMessage(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody WechatMessage message) {

        log.info("收到微信消息推送: signature={}, timestamp={}, nonce={}",
                signature, timestamp, nonce);
        log.info("消息内容: {}", message);

        return WxCallbackResp.success();
    }

}