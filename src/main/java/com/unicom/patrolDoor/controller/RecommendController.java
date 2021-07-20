package com.unicom.patrolDoor.controller;

import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.entity.Recommend;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.service.QuestionService;
import com.unicom.patrolDoor.service.RecommendService;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/4/13 9:53
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private static final Logger log = LogManager.getLogger(RecommendController.class);

    @Resource
    private RecommendService recommendService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    /**
     * @api {Post} /recommend/insert 01-推荐帖子
     * @apiVersion 1.0.0
     * @apiGroup 推荐帖子
     * @apiName 01-推荐帖子
     * @apiDescription 推荐帖子
     * @apiSuccess (入参) {Integer} questionId 帖子ID
     * @apiSuccessExample 入参示例
     * {
     * "questionId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"推荐成功"
     * "data":{}
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody Map<Object, Object> map) {
        log.info("------------------/recommend/insert-------------begin---------");

        Result result = new Result();
        Recommend recommend = new Recommend();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

        if (map.get("questionId") == null || map.get("questionId").equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("推荐失败------权限不足");
            log.info("questionId-----参数为空");
            return result;
        }
        if (map.get("userId") == null || map.get("userId").equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("推荐失败------权限不足");
            log.info("userId-----参数为空");
            return result;
        }
        User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        Question question = questionService.selectById(Integer.parseInt(map.get("questionId").toString()));
        if (user == null || user.equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("推荐失败------权限不足");
            log.info("user对象为空----数据库中不存在,userId:" + map.get("userId").toString());
            return result;
        }
        if (question == null || question.equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("question----数据库中不存在,questionId:" + map.get("questionId").toString());
            return result;
        }
        //首先查看之前是否推荐过
        //判断用户是不是超级管理员 或者是模块管理员
        Map<Integer, Integer> hashMap = new HashMap<>();
        if (StringUtils.isNotBlank(user.getTagIds())) {
            String[] strings = user.getTagIds().split(",");
            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    map.put(Integer.parseInt(strings[i]), Integer.parseInt(strings[i]));
                }
            }
        }

        if ((user.getIsNo() == 1) || (user.getIsNo() == 2 && hashMap.get(question.getTag()) != null)) {
            Recommend recom = recommendService.selectByQuestionId(Integer.parseInt(map.get("questionId").toString()));
            if (recom != null && !(recom.equals(""))) {
                try {
                    recommendService.updateRecommend(recom.getId(), df.format(new Date()));
                    result.setCode(ResultCode.SUCCESS);
                    result.setMessage("推荐成功");
                    return result;
                } catch (Exception e) {
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage("推荐失败------权限不足");
                    log.info("之前推荐过-------修改数据库失败");
                    return result;
                }
            } else {
                recommend.setQuestionId(Integer.parseInt(map.get("questionId").toString()));
                recommend.setRecommendTime(df.format(new Date()));
                try {
                    recommendService.insertRecommend(recommend);
                    result.setCode(ResultCode.SUCCESS);
                    result.setMessage("推荐成功");
                    return result;
                } catch (Exception e) {
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage("推荐失败------权限不足");
                    log.info("之前没有推荐过-------添加数据库失败");
                    return result;
                }
            }
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("推荐失败------权限不足");

        }

        log.info("------------------/recommend/insert-------------end---------");

        return result;
    }

    /**
     * @api {Post} /recommend/list 02-推荐帖子列表（取前五条）
     * @apiVersion 1.0.0
     * @apiGroup 推荐帖子
     * @apiName 02-推荐帖子列表
     * @apiDescription 推荐帖子列表
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"推荐成功"
     * "data":{
     * "id":1,
     * "title":"你好",
     * "viewCount":35
     * }
     */
    //推荐
    @PostMapping("/list")
    public Result list() {
        log.info("------------------/recommend/list-------------begin---------");

        Result result = new Result();
        try {
            List<Map<String, Object>> list = recommendService.selectList();
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("推荐成功");
            result.setData(list);
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("推荐失败");
        }
        log.info("------------------/recommend/insert-------------end---------");

        return result;
    }
}
