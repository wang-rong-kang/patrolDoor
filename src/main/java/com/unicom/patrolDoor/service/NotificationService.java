package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Notification;

import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/9 15:05
 */
public interface NotificationService {
    Integer selectByUserIdAndStatus(Integer userId, int statu);

    List<Map<String,Object>> selectListByUserIdAndStatus(Integer userId, int i);

    void updateNotification(Notification notification);

    Map<String, Object> selectByNotificationId(Integer notificationId);

    void insert(Notification notification);

    List<Map<String, Object>> selectQuestionUnReadOrReadListByUserIdAndStatus(Integer userId, Integer statu);

    void questionUpdateUnReadToRead(int notificationId);

    List<Map<String, Object>> selectQuestionUnReadOrReadListAndNum(Integer userId, Integer statu);
}
