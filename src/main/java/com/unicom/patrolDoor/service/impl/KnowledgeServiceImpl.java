package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.KnowledgeMapper;
import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;
import com.unicom.patrolDoor.service.KnowledgeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/27 15:46
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    private static final Logger log = LogManager.getLogger(KnowledgeServiceImpl.class);

    @Resource
    private KnowledgeMapper knowledgeMapper;

    @Override
    public List<Knowledge> selectAll(String name,Integer statu) {
        return knowledgeMapper.selectAll(name,statu);
    }

    @Override
    public Knowledge selectById(int id) {
        return knowledgeMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(Knowledge knowledge) {
        knowledgeMapper.insertSelective(knowledge);
    }

    @Override
    public void update(Knowledge knowledge) {
        knowledgeMapper.updateByPrimaryKeySelective(knowledge);
    }

    @Override
    public List<Knowledge> selectAllByKnowledgeStatu(int knowledgeStatu) {
        return knowledgeMapper.selectAllByKnowledgeStatu(knowledgeStatu);
    }

    @Override
    public List<Knowledge> selectByName(String knowledgeName,Integer id) {
        return knowledgeMapper.selectByName(knowledgeName,id);
    }

    @Override
    public List<Knowledge> selectAllKnowledge() {
        return knowledgeMapper.selectAllKnowledge();
    }

    @Override
    public List<Knowledge> selectAllDelete(String knowledgeName) {
        return knowledgeMapper.selectAllDelete(knowledgeName);
    }

    @Override
    public List<Knowledge> selectByNameAndIds(List list, String name, Integer statu) {
        return knowledgeMapper.selectByNameAndIds(list,name,statu);
    }


}
