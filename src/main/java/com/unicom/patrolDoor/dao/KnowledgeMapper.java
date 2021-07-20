package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Knowledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface KnowledgeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Knowledge record);

    int insertSelective(Knowledge record);

    Knowledge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Knowledge record);

    int updateByPrimaryKey(Knowledge record);

    List<Knowledge> selectAll(@Param("knowledgeName") String knowledgeName, @Param("knowledgeStatu") Integer knowledgeStatu);

    List<Knowledge> selectAllByKnowledgeStatu(@Param("knowledgeStatu") Integer knowledgeStatu);

    List<Knowledge> selectByName(@Param("knowledgeName") String knowledgeName, @Param("id") Integer id);

    List<Knowledge> selectAllKnowledge();

    List<Knowledge> selectAllDelete(@Param("knowledgeName") String knowledgeName);

    List<Knowledge> selectByNameAndIds(@Param("list") List<Integer> list, @Param("name") String name, @Param("statu") Integer statu);
}