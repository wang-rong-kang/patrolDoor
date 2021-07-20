package com.unicom.patrolDoor.service;


import com.unicom.patrolDoor.entity.Tag;

import java.util.List;

/**
 * @Author wrk
 * @Date 2021/4/8 10:32
 */
public interface TagService {

    List<Tag> selectByName(String name,Integer id);

    void save(Tag tag);

    void update(Tag tag);

    Tag selectById(int id);

    List<Tag> selectAll(int statu, String name);

    List<Tag> selectAllByStatu(int i);

    List<Tag> selectAllByIdsAndNameAndStatu(List list, int statu, String name);

}
