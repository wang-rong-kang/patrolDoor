package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NotificationMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Notification record);

    int insertSelective(Notification record);

    Notification selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Notification record);

    int updateByPrimaryKey(Notification record);

    Integer selectByUserIdAndStatus(@Param("userId") Integer userId, @Param("statu") Integer statu);

    List<Map<String, Object>> selectListByUserIdAndStatus(@Param("userId") Integer userId, @Param("statu") Integer statu);

    Map<String, Object> selectByNotificationId(@Param("notificationId") Integer notificationId);

    List<Map<String, Object>> selectQuestionUnReadOrReadListByUserIdAndStatus(@Param("userId") Integer userId, @Param("statu") Integer statu);

    void questionUpdateUnReadToRead(@Param("notificationId") Integer notificationId);

    List<Map<String, Object>> selectQuestionUnReadOrReadListAndNum(@Param("userId") Integer userId, @Param("statu") Integer statu);
}