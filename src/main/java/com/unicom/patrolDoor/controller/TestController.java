package com.unicom.patrolDoor.controller;

import com.unicom.patrolDoor.entity.Test;
import com.unicom.patrolDoor.service.TestService;
import com.unicom.patrolDoor.utils.HttpClientUtil;
import com.unicom.patrolDoor.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/6/9 15:41
 */

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private TestService testService;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    /**
     * Get请求
     */
    @RequestMapping(value = "/transferByGet", method = RequestMethod.GET)
    public Result transferByGet() {
        Result result = new Result();
        return result;
    }


    /**
     * Post请求
     */
    @RequestMapping(value = "/transferByPost", method = RequestMethod.POST)
    public Result transferByPost() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        Map map = new HashMap<>();
        map.put("test", "test");
        Result result = httpClientUtil.transferByPost(map, "http://192.168.199.139:8030/user/test");
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Result insert() {
        Result result = new Result();
        Test test=new Test();
        test.setA("你好");
        test.setB("大家好");
        int num=testService.insert(test);
        Map map = new HashMap<>();
        map.put("num", num);
        result.setData(map);
        return result;
    }
}
