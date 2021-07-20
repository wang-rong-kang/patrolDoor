package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.CommentMapper;
import com.unicom.patrolDoor.dao.QuestionMapper;
import com.unicom.patrolDoor.dao.UserMapper;
import com.unicom.patrolDoor.entity.Comment;
import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import com.unicom.patrolDoor.vo.QuesAndComVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/24 18:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Result deleteByUserIdAndQuestId(QuesAndComVo quesAndComVo) {
        Result result = new Result();
        //首先判断用户是管理员  还是普通用户
        User user = userMapper.selectByPrimaryKey(quesAndComVo.getCommentId());
        Map<Integer, Integer> map = new HashMap<>();
        if (StringUtils.isNotBlank(user.getTagIds())) {
            String[] strings = user.getTagIds().split(",");
            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    map.put(Integer.parseInt(strings[i]), Integer.parseInt(strings[i]));
                }
            }
        }
        //删除的帖子
        if (quesAndComVo.getType() == 1) {
            Question question = questionMapper.selectByPrimaryKey(quesAndComVo.getQuestionId());
            /**
             * 只要是用户不是管理员,以及此帖子不是登录用户发表的  不能删除
             */
            if ((question.getCreator().equals(quesAndComVo.getCommentId())) || (user.getIsNo() == 1) || (user.getIsNo() == 2 && map.get(question.getTag()) != null)) {
                try {
                    //直接删除帖子 以及包含的回帖
                    Question q = new Question();
                    q.setId(quesAndComVo.getQuestionId());
                    q.setType(1);
                    questionMapper.updateByPrimaryKeySelective(q);

                    //删除回帖
                    commentMapper.updateByParentIdAndStatu(quesAndComVo.getQuestionId(), 1);
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动进行回滚
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage("删除失败");
                    return result;
                }
            } else {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("删除失败，您没有此权限");
                return result;
            }
        } else {
            //删除的回复
            Comment comment = commentMapper.selectByPrimaryKey(quesAndComVo.getQuestionId());
            if ((comment.getCommentator().equals(quesAndComVo.getCommentId())) || (user.getIsNo() == 1) || (user.getIsNo() == 2 && map.get(quesAndComVo.getQuestionId()) != null)) {
                try {
                    Comment c = new Comment();
                    c.setId(quesAndComVo.getQuestionId());
                    c.setType(1);
                    commentMapper.updateByPrimaryKeySelective(c);
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动进行回滚
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage("删除失败");
                    return result;
                }
            } else {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("删除失败，您没有此权限");
                return result;
            }

        }

        result.setCode(ResultCode.SUCCESS);
        result.setMessage("删除成功");
        return result;
    }

    //根据用户Id  查找用户
    @Override
    public User selectUserById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public User selectUserByNumberAndPassword(String userName, String password) {
        return userMapper.selectUserByNumberAndPassword(userName, password);
    }

    @Override
    public void updateIsOnLineByUserId(Integer id) {
        userMapper.updateIsOnLineByUserId(id);
    }

    @Override
    public void updateIsOnLineById(Integer id) {
        userMapper.updateIsOnLineById(id);
    }

    @Override
    public List<User> selectByNumber(String number) {
        return userMapper.selectByNumber(number);
    }

    @Override
    public void insertUser(User user) {
        userMapper.insertSelective(user);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public List<User> selectByStatu(Integer statu, String name, String number) {
        return userMapper.selectByStatu(statu, name, number);
    }

    @Override
    public User selectUserByNumberAndEmail(String number, String email) {
        return userMapper.selectUserByNumberAndEmail(number, email);
    }

    @Override
    public List<User> selectTags(int tags) {
        return userMapper.selectTags(tags);
    }

    @Override
    public void updateTagsOnUserId(String tags, Integer id) {
        userMapper.updateTagsOnUserId(tags,id);
    }

    @Override
    public void updateTagsByUserId(Integer id) {
        userMapper.updateTagsByUserId(id);
    }

    @Override
    public List<User> selectTagss(Integer id) {
        return userMapper.selectTagss(id);
    }

    @Override
    public void updateTagsById(String tags, Integer id) {
        userMapper.updateTagsById(tags,id);
    }

    @Override
    public void updateTagsOnId(Integer id) {
        userMapper.updateTagsOnId(id);
    }

    @Override
    public List<User> selectUserByStatu() {
        return userMapper.selectUserByStatu();
    }

    @Override
    public Integer selectMaxId() {
        return userMapper.selectMaxId();
    }
}
