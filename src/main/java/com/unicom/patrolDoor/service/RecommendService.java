package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Recommend;

import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/13 9:56
 */
public interface RecommendService {
    void insertRecommend(Recommend recommend);

    Recommend selectByQuestionId(int questionId);

    void updateRecommend(Integer id,String recommendTime);

    List<Map<String, Object>> selectList();
}
