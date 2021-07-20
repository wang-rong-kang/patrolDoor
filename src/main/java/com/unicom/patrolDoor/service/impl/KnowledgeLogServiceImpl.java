package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.KnowledgeLogMapper;
import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;
import com.unicom.patrolDoor.service.KnowledgeLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/7 11:30
 */
@Service
public class KnowledgeLogServiceImpl implements KnowledgeLogService {
    @Resource
    private KnowledgeLogMapper knowledgeLogMapper;

    @Override
    public void insertKnowledgeLog(KnowledgeLog knowledgeLog) {
        knowledgeLogMapper.insertSelective(knowledgeLog);
    }

    @Override
    public List<KnowledgeLog> selectByUserId(int userId) {
        return knowledgeLogMapper.selectByUserId(userId);
    }

    @Override
    public List<KnowledgeLog> selectByUserIdAndKnowledgeName(Integer userId, String name) {
        return knowledgeLogMapper.selectByUserIdAndKnowledgeName(userId,name);
    }
}
