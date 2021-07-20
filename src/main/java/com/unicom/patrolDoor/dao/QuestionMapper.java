package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface QuestionMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKey(Question record);

    List<Question> selectAllQuestion(@Param("tag") Integer tag, @Param("title") String title);

    void updateCommentCount(@Param("id") Integer id);

    List<Question> selectAllQuestionByViewCount(@Param("tag") Integer tag);

    Integer sendQuestionNum(@Param("begin") String begin, @Param("end") String end);

    List<Map<Object, Object>> selectQuestionByHot();

    void updateViewNumById(@Param("id") Integer id);

    List<Question> selectByTagId(@Param("tagId") Integer tagId);

    void updateQuestionByTagId(@Param("tagId") Integer tagId, @Param("updateTime") String updateTime);

    List<Question> selectByNameAndStatu(@Param("name") String name, @Param("userId") Integer userId);

    List<Question> selectByName(@Param("title") String title);

    void updateTagByQuestionAndTag(@Param("questionId") Integer questionId, @Param("tag") Integer tag);

    Question selectLastQuestion();

    List<Question> selectAllQuestionByTagAndTypeAndUserId(@Param("tag") Integer tag, @Param("type") Integer type, @Param("userId") Integer userId, @Param("title") String title);


}