package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.QuestionMapper;
import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/24 16:02
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionMapper questionMapper;

    @Override
    public List<Question> selectAllQuestion(Integer tag,String title) {
        return questionMapper.selectAllQuestion(tag,title);
    }

    //动态添加帖子的属性值
    @Override
    public void save(Question question) {
        questionMapper.insertSelective(question);
    }

    @Override
    public Question selectById(Integer id) {
        return questionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Question> selectAllQuestionByViewCount(Integer tag) {
        return questionMapper.selectAllQuestionByViewCount(tag);
    }

    @Override
    public Integer sendQuestionNum(String begin, String end) {
        return questionMapper.sendQuestionNum(begin,end);
    }

    @Override
    public List<Map<Object, Object>> selectQuestionByHot() {
        return questionMapper.selectQuestionByHot();
    }

    @Override
    public void updateViewNumById(Integer id) {
        questionMapper.updateViewNumById(id);
    }

    @Override
    public List<Question> selectByTagId(int tagId) {
        return questionMapper.selectByTagId(tagId);
    }

    @Override
    public void updateQuestionByTagId(int tagId,String time) {
        questionMapper.updateQuestionByTagId(tagId,time);
    }

    @Override
    public List<Question> selectByNameAndStatu(String name,Integer userId) {
        return  questionMapper.selectByNameAndStatu(name,userId);
    }

    @Override
    public List<Question> selectByName(String title) {
        return  questionMapper.selectByName(title);
    }

    @Override
    public void updateTagByQuestionAndTag(int questionId, int tag) {
        questionMapper.updateTagByQuestionAndTag(questionId,tag);
    }

    @Override
    public Question selectLastQuestion() {
        return questionMapper.selectLastQuestion();
    }

    @Override
    public List<Question> selectAllQuestionByTagAndTypeAndUserId(Integer tag, Integer type, Integer userId,String title) {
        return questionMapper.selectAllQuestionByTagAndTypeAndUserId(tag,type,userId,title);
    }


}
