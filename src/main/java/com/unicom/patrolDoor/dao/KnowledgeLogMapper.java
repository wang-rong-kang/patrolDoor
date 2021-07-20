package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface KnowledgeLogMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(KnowledgeLog record);

    int insertSelective(KnowledgeLog record);

    KnowledgeLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(KnowledgeLog record);

    int updateByPrimaryKey(KnowledgeLog record);

    List<KnowledgeLog> selectByUserId(@Param("userId") Integer userId);

    List<KnowledgeLog> selectByUserIdAndKnowledgeName(@Param("userId") Integer userId, @Param("name") String name);
}