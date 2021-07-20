package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.TagMapper;
import com.unicom.patrolDoor.entity.Tag;
import com.unicom.patrolDoor.service.TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wrk
 * @Date 2021/4/8 10:33
 */
@Service
public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Override
    public List<Tag> selectByName(String name,Integer id) {
        return tagMapper.selectByName(name,id);
    }

    @Override
    public void save(Tag tag) {
        tagMapper.insertSelective(tag);
    }

    @Override
    public void update(Tag tag) {
        tagMapper.updateByPrimaryKeySelective(tag);
    }

    @Override
    public Tag selectById(int id) {
        return tagMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Tag> selectAll(int statu, String name) {
        return tagMapper.selectAll(statu, name);
    }

    @Override
    public List<Tag> selectAllByStatu(int i) {
        return tagMapper.selectAllByStatu(i);
    }

    @Override
    public List<Tag> selectAllByIdsAndNameAndStatu(List list, int statu, String name) {
        return tagMapper.selectAllByIdsAndNameAndStatu(list,statu,name);
    }

}
