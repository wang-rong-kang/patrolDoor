package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.VoteLogMapper;
import com.unicom.patrolDoor.entity.VoteLog;
import com.unicom.patrolDoor.service.VoteLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/16 11:14
 */
@Service
public class VoteLogServiceImpl implements VoteLogService {
    @Resource
    private VoteLogMapper voteLogMapper;

    @Override
    public void insert(VoteLog voteLog) {
        voteLogMapper.insertSelective(voteLog);
    }

    @Override
    public List<VoteLog> selectByOptionAndVoteId(int voteId, String option) {
       return voteLogMapper.selectByOptionAndVoteId(voteId,option);
    }

    @Override
    public List<VoteLog> selectByVoteIdAndUserId(int voteId, int userId) {
        return voteLogMapper.selectByVoteIdAndUserId(voteId,userId);
    }
}
