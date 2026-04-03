package com.leon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leon.domain.aggregate.GameUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GameUserMapper extends BaseMapper<GameUser> {

    @Select("SELECT * FROM game_user WHERE open_id = #{openId}")
    GameUser selectByOpenId(@Param("openId") String openId);

    @Update("UPDATE game_user SET coins = coins + 1 WHERE open_id = #{openId}")
    int addCoin(@Param("openId") String openId);

    @Update("UPDATE game_user SET coins = coins - #{price} WHERE open_id = #{openId} AND coins >= #{price}")
    int deductCoins(@Param("openId") String openId, @Param("price") int price);

    @Select("SELECT * FROM game_user WHERE score_time >= #{startTime} AND score_time < #{endTime} ORDER BY high_score DESC LIMIT 50")
    List<GameUser> selectMonthlyTop50(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM game_user WHERE score_time >= #{startTime} AND score_time < #{endTime} ORDER BY high_score DESC LIMIT 50")
    List<GameUser> selectWeeklyTop50(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM game_user WHERE high_score > 0 ORDER BY high_score DESC LIMIT 50")
    List<GameUser> selectAllTimeTop50();

}
