package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.TestMapper;
import com.unicom.patrolDoor.entity.Test;
import com.unicom.patrolDoor.service.TestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author wrk
 * @Date 2021/6/24 9:12
 */
@Service
public class TestServiceImpl implements TestService {
    @Resource
    private TestMapper testMapper;

    @Override
    public int insert(Test test) {
        int num =testMapper.insertTest(test);
        System.out.println(num+"-------------------"+test.getId());
        return test.getId();
    }
}
