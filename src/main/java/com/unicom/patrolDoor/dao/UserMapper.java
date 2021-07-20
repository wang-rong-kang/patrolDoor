package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectUserByNumberAndPassword(@Param("userName") String userName, @Param("password") String password);

    void updateIsOnLineByUserId(@Param("id") Integer id);

    void updateIsOnLineById(@Param("id") Integer id);

    List<User> selectByNumber(@Param("number") String number);

    List<User> selectByStatu(@Param("statu") Integer statu, @Param("name") String name, @Param("number") String number);

    User selectUserByNumberAndEmail(@Param("number") String number, @Param("email") String email);

    List<User> selectTags(@Param("tags") Integer tags);

    void updateTagsOnUserId(@Param("tags") String tags, @Param("id") Integer id);

    void updateTagsByUserId(@Param("id") Integer id);

    List<User> selectTagss(@Param("tags") Integer tags);

    void updateTagsById(@Param("tags") String tags, @Param("id") Integer id);

    void updateTagsOnId(@Param("id") Integer id);

    List<User> selectUserByStatu();

    Integer selectMaxId();

    List<User> selectByStatuAndIsLocal();


}