package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/7 11:29
 */
public interface KnowledgeLogService {
    void insertKnowledgeLog(KnowledgeLog knowledgeLog);

    List<KnowledgeLog> selectByUserId(int userId);

    List<KnowledgeLog> selectByUserIdAndKnowledgeName(Integer userId, String name);
}
