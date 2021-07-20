package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Vote;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VoteMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Vote record);

    int insertSelective(Vote record);

    Vote selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);

    List<Map<Object, Object>> selectList(@Param("theme") String theme, @Param("userName") String userName, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("voteStatus") String voteStatus);

    void updateVoteStatusById(@Param("voteStatus") Integer voteStatus, @Param("id") Integer id);

    List<Map<Object, Object>> selectHistoryList(@Param("theme") String theme, @Param("userId") Integer userId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("voteStatus") String voteStatus);

    void updateVoteStatuByTime(@Param("nowTime") String nowTime);
}