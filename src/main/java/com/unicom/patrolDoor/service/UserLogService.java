package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.UserLog;

/**
 * @Author wrk
 * @Date 2021/5/28 11:15
 */
public interface UserLogService {
    void insertUserLog(UserLog userLog);

    void updateUserLog(UserLog userLog);

    Integer selectMaxId();

    Integer selectTodayUserOnLineNum(String begin,String end);
}
