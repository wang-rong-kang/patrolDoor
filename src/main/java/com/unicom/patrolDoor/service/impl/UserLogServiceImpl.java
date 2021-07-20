package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.UserLogMapper;
import com.unicom.patrolDoor.entity.UserLog;
import com.unicom.patrolDoor.service.UserLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author wrk
 * @Date 2021/5/28 11:16
 */
@Service
public class UserLogServiceImpl implements UserLogService {
    @Resource
    private UserLogMapper userLogMapper;

    @Override
    public void insertUserLog(UserLog userLog) {
        userLogMapper.insertSelective(userLog);
    }

    @Override
    public void updateUserLog(UserLog userLog) {
        userLogMapper.updateByPrimaryKeySelective(userLog);
    }

    @Override
    public Integer selectMaxId() {
        return userLogMapper.selectMaxId();
    }

    @Override
    public Integer selectTodayUserOnLineNum(String begin,String end) {
        return userLogMapper.selectTodayUserOnLineNum(begin, end);
    }
}
