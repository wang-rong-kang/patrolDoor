package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Comment;
import com.unicom.patrolDoor.utils.Result;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/3/24 17:21
 */
public interface CommentService {
    List<Comment> selectByParentId(Integer id,Integer type);

    Comment selectByCommentId(Integer comment_id);

    Integer replyNum(String begin, String end, Integer type);

    Result save(Comment comment);
}
