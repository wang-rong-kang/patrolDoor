package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.Question;

import javax.management.ObjectName;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/24 16:02
 */
public interface QuestionService {
    List<Question> selectAllQuestion(Integer tag,String title);

    void save(Question question);

    Question selectById(Integer id);

    List<Question> selectAllQuestionByViewCount(Integer tag);

    Integer sendQuestionNum(String begin, String end);

    List<Map<Object, Object>> selectQuestionByHot();

    void updateViewNumById(Integer id);

    List<Question> selectByTagId(int tagId);

    void updateQuestionByTagId(int tagId,String time);

    List<Question> selectByNameAndStatu(String name,Integer userId);

    List<Question> selectByName(String title);

    void updateTagByQuestionAndTag(int questionId, int tag);

    Question selectLastQuestion();


    List<Question> selectAllQuestionByTagAndTypeAndUserId(Integer tag, Integer type, Integer userId,String title);


}
