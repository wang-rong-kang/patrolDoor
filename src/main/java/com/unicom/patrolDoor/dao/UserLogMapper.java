package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.UserLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/5/28 11:03
 */
public interface UserLogMapper {
    void insertSelective(UserLog userLog);

    void updateByPrimaryKeySelective(UserLog userLog);

    Integer selectMaxId();

    Integer selectTodayUserOnLineNum(@Param("begin") String begin,@Param("end") String end);

    List<UserLog> selectByBeginTimeAndIsOnLineAndEndTime(@Param("yesterdayTime") String yesterdayTime,@Param("isNo")  Integer isNo);
}
