package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.VoteLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoteLogMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(VoteLog record);

    int insertSelective(VoteLog record);

    VoteLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VoteLog record);

    int updateByPrimaryKey(VoteLog record);

    List<VoteLog> selectByOptionAndVoteId(@Param("voteId") Integer voteId, @Param("option") String option);

    List<VoteLog> selectByVoteIdAndUserId(@Param("voteId") Integer voteId, @Param("userId") Integer userId);

}