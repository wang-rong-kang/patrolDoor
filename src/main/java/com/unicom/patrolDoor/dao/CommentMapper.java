package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {


    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    List<Comment> selectByParentId(@Param("id") Integer id, @Param("type") Integer type);

    void updateCommentCount(@Param("id") Integer id);

    void updateByParentIdAndStatu(@Param("questionId") Integer questionId,@Param("statu") Integer statu);

    Integer selectMaxId();

    Integer replyNum(@Param("begin") String begin,@Param("end") String end,@Param("type") Integer type);

}