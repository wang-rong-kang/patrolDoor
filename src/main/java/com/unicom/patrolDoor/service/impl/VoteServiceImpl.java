package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.VoteMapper;
import com.unicom.patrolDoor.entity.Vote;
import com.unicom.patrolDoor.service.VoteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/6/15 15:23
 */
@Service
public class VoteServiceImpl implements VoteService {
    @Resource
    private VoteMapper voteMapper;


    @Override
    public void insert(Vote vote) {
        voteMapper.insertSelective(vote);
    }

    @Override
    public Vote selectById(int id) {
        return voteMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Map<Object, Object>> selectList(String theme,String userName,String beginTime,String endTime,String voteStatus) {
        return voteMapper.selectList(theme,userName,beginTime,endTime,voteStatus);
    }

    @Override
    public void updateVoteStatusById(int voteStatus, int id) {
        voteMapper.updateVoteStatusById(voteStatus,id);
    }

    @Override
    public void updateVote(Vote vote) {
        voteMapper.updateByPrimaryKeySelective(vote);
    }

    @Override
    public List<Map<Object, Object>> selectHistoryList(String theme, int userId,String beginTime,String endTime,String voteStatus) {
        return voteMapper.selectHistoryList(theme,userId,beginTime,endTime,voteStatus);
    }

    @Override
    public void updateVoteStatuByTime(String nowTime) {
        voteMapper.updateVoteStatuByTime(nowTime);
    }
}
