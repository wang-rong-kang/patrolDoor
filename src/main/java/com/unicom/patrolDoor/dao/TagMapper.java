package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    List<Tag> selectByName(@Param("name") String name, @Param("id") Integer id);

    List<Tag> selectAll(@Param("statu") Integer statu, @Param("name") String name);

    List<Tag> selectAllByStatu(@Param("statu") Integer statu);

    List<Tag> selectAllByIdsAndNameAndStatu(@Param("list") List list, @Param("statu") Integer statu, @Param("name") String name);

}