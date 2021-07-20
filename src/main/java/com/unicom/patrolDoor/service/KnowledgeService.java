package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;

import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/27 15:46
 */
public interface KnowledgeService {
    List<Knowledge> selectAll(String name,Integer statu);

    Knowledge selectById(int id);

    void save(Knowledge knowledge);

    void update(Knowledge knowledge);

    List<Knowledge> selectAllByKnowledgeStatu(int knowledgeStatu);

    List<Knowledge> selectByName(String knowledgeName,Integer id);

    List<Knowledge> selectAllKnowledge();

    List<Knowledge> selectAllDelete(String knowledgeName);

    //Map<Object,Object>
    List<Knowledge> selectByNameAndIds(List list, String name, Integer statu);
}
