package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.CommentMapper;
import com.unicom.patrolDoor.dao.NotificationMapper;
import com.unicom.patrolDoor.dao.QuestionMapper;
import com.unicom.patrolDoor.entity.Comment;
import com.unicom.patrolDoor.entity.Notification;
import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.service.CommentService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @Author wrk
 * @Date 2021/3/24 17:21
 */
@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger log = LogManager.getLogger(CommentServiceImpl.class);

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public List<Comment> selectByParentId(Integer id, Integer type) {
        return commentMapper.selectByParentId(id, type);
    }

    @Override
    public Comment selectByCommentId(Integer comment_id) {
        return commentMapper.selectByPrimaryKey(comment_id);
    }

    @Override
    public Integer replyNum(String begin, String end, Integer type) {
        return commentMapper.replyNum(begin, end, type);
    }

    /**
     * 创建回帖
     * 判断是回复的帖子 还是回复的评论
     */
    @Override
    public Result save(Comment comment) {
        Result result = new Result();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        comment.setGmtCreate(df.format(new Date()));
        if (comment.getType() == 1) {
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            //回复帖子  相当于AB
            commentMapper.insertSelective(comment);
            Integer num = commentMapper.selectMaxId();
            questionMapper.updateCommentCount(comment.getParentId());//将questoion中comment_count的数量加1

            sendNotification(comment.getCommentator(), question.getCreator(), num,comment.getType(),comment.getQuestion());
            log.info("通知成功");
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
            return result;

        } else if (comment.getType() == 2) {
            /**
             * 回复的评论  分两种  a: type是1的评论 , b:type类型是2 的评论
             */
            commentMapper.insertSelective(comment);
            Integer num = commentMapper.selectMaxId();
            commentMapper.updateCommentCount(comment.getParentId());//将questoion中comment_count的数量加1

            Comment com = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (com.getType() == 1) {//ABC 三层
                //给回复帖子的用户 以及 发帖子用户发通知
                sendNotification(comment.getCommentator(), com.getCommentator(), num,com.getType(),comment.getQuestion());//给回复帖子的用户下通知
                Question comQuestUser = questionMapper.selectByPrimaryKey(com.getParentId());
                sendNotification(comment.getCommentator(), comQuestUser.getCreator(), num,com.getType(),comment.getQuestion());//给发帖子用户下通知
                log.info("通知成功");
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("成功");
                return result;
            } else {//ABCD  四层
                sendNotification(comment.getCommentator(), com.getCommentator(), num,com.getType(),comment.getQuestion());//给回复帖子的用户下通知
                Comment comB = commentMapper.selectByPrimaryKey(com.getParentId());
                Question comQuestUser = questionMapper.selectByPrimaryKey(comB.getParentId());
                sendNotification(comment.getCommentator(), comQuestUser.getCreator(), num,com.getType(),comment.getQuestion());//给发帖子用户下通知
                log.info("通知成功");
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("成功");
                return result;
            }

        } else {
            log.info("传输的参数错误，没有parentId");
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("服务器内部错误");
            return result;
        }
    }

    /**
     * 回复帖子 下通知
     *
     * @param userId
     * @param creator
     */
    public void sendNotification(Integer userId, Integer creator, Integer commentId,Integer type,Integer question) {
        Notification notification = new Notification();
        notification.setNotifier(userId);//回帖子用户
        notification.setReceiver(creator);//发帖子用户
        notification.setCommentId(commentId);//帖子或者是评论Id
        notification.setStatus(0);
        notification.setType(type);
        notification.setQuestionId(question);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        notification.setGmtCreate(df.format(new Date()));
        notificationMapper.insertSelective(notification);
    }
}
