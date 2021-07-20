package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Recommend;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RecommendMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Recommend record);

    int insertSelective(Recommend record);

    Recommend selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Recommend record);

    int updateByPrimaryKey(Recommend record);

    Recommend selectByQuestionId(@Param("questionId") Integer questionId);

    void updateRecommendById(@Param("id") Integer id, @Param("recommendTime") String recommendTime);

    List<Map<String, Object>> selectList();
}