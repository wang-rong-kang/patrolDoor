package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.RecommendMapper;
import com.unicom.patrolDoor.entity.Recommend;
import com.unicom.patrolDoor.service.RecommendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/13 9:57
 */
@Service
public class RecommendServiceImpl implements RecommendService {
    @Resource
    private RecommendMapper recommendMapper;
    @Override
    public void insertRecommend(Recommend recommend) {
        recommendMapper.insertSelective(recommend);
    }

    @Override
    public Recommend selectByQuestionId(int questionId) {
        return recommendMapper.selectByQuestionId(questionId);
    }

    @Override
    public void updateRecommend(Integer id,String recommendTime) {
        recommendMapper.updateRecommendById(id,recommendTime);
    }

    @Override
    public List<Map<String, Object>> selectList() {
        return recommendMapper.selectList();
    }
}
