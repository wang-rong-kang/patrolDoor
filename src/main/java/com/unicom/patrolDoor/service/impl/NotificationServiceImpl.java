package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.NotificationMapper;
import com.unicom.patrolDoor.entity.Notification;
import com.unicom.patrolDoor.service.NotificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/9 15:05
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public Integer selectByUserIdAndStatus(Integer userId, int statu) {
        return notificationMapper.selectByUserIdAndStatus(userId,statu);
    }

    @Override
    public List<Map<String,Object>> selectListByUserIdAndStatus(Integer userId, int statu) {
        return notificationMapper.selectListByUserIdAndStatus(userId,statu);
    }

    @Override
    public void updateNotification(Notification notification) {
        notificationMapper.updateByPrimaryKeySelective(notification);
    }

    @Override
    public Map<String, Object> selectByNotificationId(Integer notificationId) {
        return notificationMapper.selectByNotificationId(notificationId);
    }

    @Override
    public void insert(Notification notification) {
        notificationMapper.insertSelective(notification);
    }

    @Override
    public List<Map<String, Object>> selectQuestionUnReadOrReadListByUserIdAndStatus(Integer userId, Integer statu) {
        return notificationMapper.selectQuestionUnReadOrReadListByUserIdAndStatus(userId,statu);
    }

    @Override
    public void questionUpdateUnReadToRead(int notificationId) {
        notificationMapper.questionUpdateUnReadToRead(notificationId);
    }

    @Override
    public List<Map<String, Object>> selectQuestionUnReadOrReadListAndNum(Integer userId, Integer statu) {
        return notificationMapper.selectQuestionUnReadOrReadListAndNum(userId,statu);
    }
}
