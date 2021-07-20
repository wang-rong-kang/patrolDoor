package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.TagLogMapper;
import com.unicom.patrolDoor.entity.Tag;
import com.unicom.patrolDoor.entity.TagLog;
import com.unicom.patrolDoor.service.TagLogSerive;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wrk
 * @Date 2021/6/7 15:30
 */
@Service
public class TagLogSeriveImpl implements TagLogSerive {
    @Resource
    private TagLogMapper tagLogMapper;

    @Override
    public void insertTagLog(TagLog tagLog) {
        tagLogMapper.insertSelective(tagLog);
    }

    @Override
    public List<TagLog> selectByUserIdAndNameAndStatu(String name, Integer userId) {
        return tagLogMapper.selectByUserIdAndNameAndStatu(name,userId);
    }
}
