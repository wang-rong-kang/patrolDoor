package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.VoteLog;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/16 11:14
 */
public interface VoteLogService {
    void insert(VoteLog voteLog);

    List<VoteLog> selectByOptionAndVoteId(int voteId, String option);

    List<VoteLog> selectByVoteIdAndUserId(int voteId, int userId);

}
