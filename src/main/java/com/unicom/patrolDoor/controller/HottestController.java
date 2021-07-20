package com.unicom.patrolDoor.controller;

import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.service.QuestionService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/1 17:29
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/hottestQuestion")
public class HottestController {
    private static final Logger log = LogManager.getLogger(HottestController.class);

    @Resource
    private QuestionService questionService;

    /**
     * @api {POST} /hottestQuestion/list 01-最热
     * @apiVersion 1.0.0
     * @apiGroup hottestQuestion
     * @apiName 01-最热
     * @apiDescription 最热
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"回复成功，已通知用户"
     * "data":{
     *     "id": 14,
     *     "title": "地方VC",
     *     "gmtCreate": null,
     *     "gmtModified": null,
     *     "creator": 2,
     *     "commentCount": null,
     *     "viewCount": null,
     *     "likeCount": null,
     *     "tag": null,
     *     "sticky": 0,
     *     "description": "儿",
     *     "type": null,
     *     "userName": "bc"
     *   }
     * }
     */
    @PostMapping(value = "/list")
    @ResponseBody
    public Result questionList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/hottestQuestion/list-------------begin---------");

        Result result = new Result();
        //数据库查询浏览量最多的帖子 倒叙排列
        Integer tag=0;
        if(map.get("tag")!=null&&!(map.get("tag").equals(""))){
            tag=Integer.parseInt(map.get("tag").toString());
        }
        List<Question> list = questionService.selectAllQuestionByViewCount(tag);
        for(Question question:list){
            if(question.getHeadFile()==null||question.getHeadFile().equals("")){
                question.setHeadFile("");
            }
        }
        result.setData(list);
        result.setMessage("success");
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/hottestQuestion/list-------------end---------");

        return result;
    }

    /**
     * @api {POST} /hottestQuestion/hot 02-热门
     * @apiVersion 1.0.0
     * @apiGroup hottestQuestion
     * @apiName 02-热门
     * @apiDescription 热门
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":""
     * "data":{
     *     "id": 14,
     *     "title": "地方VC",
     *     "gmtCreate": null,
     *     "gmtModified": null,
     *     "creator": 2,
     *     "commentCount": null,
     *     "viewCount": null,
     *     "likeCount": null,
     *     "tag": null,
     *     "sticky": 0,
     *     "description": "儿",
     *     "type": null,
     *     "userName": "bc"
     *   }
     * }
     */
    @PostMapping(value = "/hot")
    public Result hot() {
        log.info("------------------/hottestQuestion/hot-------------begin---------");

        Result result = new Result();
        //数据库查询浏览量最多的前五个帖子 倒叙排列
        List<Map<Object,Object>> list = questionService.selectQuestionByHot();
        result.setData(list);
        result.setMessage("success");
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/hottestQuestion/hot-------------end---------");

        return result;
    }
}
