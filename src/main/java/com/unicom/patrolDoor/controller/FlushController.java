package com.unicom.patrolDoor.controller;

import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/6/24 16:23
 */
@RestController
@RequestMapping("/flush")
public class FlushController {

    @RequestMapping(value = "/flush", method = RequestMethod.POST)
    @ResponseBody
    public Result questionUpdateUnReadToRead(@RequestBody Map<Object, Object> map) {

        Result result = new Result();
        result.setCode(ResultCode.SUCCESS);
        result.setData("刷新成功");
        result.setMessage("用来刷新");
        return result;
    }
}
