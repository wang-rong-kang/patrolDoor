package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Vote;

import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/6/15 15:23
 */
public interface VoteService {
    void insert(Vote vote);

    Vote selectById(int id);

    List<Map<Object, Object>> selectList(String theme,String userName,String beginTime,String endTime,String voteStatus);

    void updateVoteStatusById(int voteStatus, int id);

    void updateVote(Vote vote);

    List<Map<Object, Object>> selectHistoryList(String theme, int userId,String beginTime,String endTime,String voteStatus);

    void updateVoteStatuByTime(String nowTime);
}
