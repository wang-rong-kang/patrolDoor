package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.vo.QuesAndComVo;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/3/24 18:04
 */
public interface UserService {

    Result deleteByUserIdAndQuestId(QuesAndComVo quesAndComVo);

    User selectUserById(Integer userId);

    User selectUserByNumberAndPassword(String userName, String password);

    void updateIsOnLineByUserId(Integer id);

    void updateIsOnLineById(Integer id);

    List<User> selectByNumber(String number);

    void insertUser(User user);

    void updateUser(User user);

    List<User> selectByStatu(Integer statu,String name,String number);

    User selectUserByNumberAndEmail(String number, String email);

    List<User> selectTags(int tags);

    void updateTagsOnUserId(String tags, Integer id);

    void updateTagsByUserId(Integer id);

    List<User> selectTagss(Integer id);

    void updateTagsById(String tags, Integer id);

    void updateTagsOnId(Integer id);

    List<User> selectUserByStatu();

    Integer selectMaxId();


}
