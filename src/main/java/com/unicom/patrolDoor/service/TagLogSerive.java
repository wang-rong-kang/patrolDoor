package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Tag;
import com.unicom.patrolDoor.entity.TagLog;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/7 15:29
 */
public interface TagLogSerive {
    void insertTagLog(TagLog tagLog);

    List<TagLog> selectByUserIdAndNameAndStatu(String name, Integer userId);
}
