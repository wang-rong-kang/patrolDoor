package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.TagLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TagLogMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(TagLog record);

    int insertSelective(TagLog record);

    TagLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TagLog record);

    int updateByPrimaryKey(TagLog record);

    List<TagLog> selectByUserIdAndNameAndStatu(@Param("name") String name, @Param("userId") Integer userId);
}