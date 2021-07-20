package com.unicom.patrolDoor.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.*;
import com.unicom.patrolDoor.service.*;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/30 17:32
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private static final Logger log = LogManager.getLogger(NotificationController.class);

    @Resource
    private NotificationService notificationService;
    @Resource
    private TagService tagService;
    @Resource
    private QuestionService questionService;
    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;

    @Resource
    private FileInfoService fileInfoService;
    /**
     * 未读通知的数量
     *
     * @return
     */
    /**
     * @api {POST} /notification/unReadNum 01-未读通知数量
     * @apiVersion 1.0.0
     * @apiGroup 通知
     * @apiName 01-未读通知数量
     * @apiDescription 未读通知数量
     * @apiSuccess (入参) {Integer} userId 当前登陆用户ID
     * @apiSuccessExample 入参示例
     * {
     * "userId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":""
     * "data":{
     * "id": 150
     * }
     * }
     */
    @RequestMapping(value = "/unReadNum", method = RequestMethod.POST)
    @ResponseBody
    public Result unReadNum(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/unReadNum-------------begin---------");

        Result result = new Result();
        Integer userId = Integer.parseInt(map.get("userId").toString());
        Integer num = notificationService.selectByUserIdAndStatus(userId, 0);
        result.setCode(ResultCode.SUCCESS);
        result.setData(num);
        result.setMessage("您有" + num + "条未读通知");
        log.info("------------------/notification/unReadNum-------------end---------");

        return result;
    }

    /**
     * 未读和已读通知的列表
     *
     * @return
     */
    /**
     * @api {POST} /notification/unReadList 02-未读和已读通知的列表
     * @apiVersion 1.0.0
     * @apiGroup 通知
     * @apiName 02-未读和已读通知的列表
     * @apiDescription 未读和已读通知的列表
     * @apiSuccess (入参) {Integer} pageNo 当前页数
     * @apiSuccess (入参) {Integer} pageNum 每页多少条
     * @apiSuccess (入参) {Integer} userId 当前登陆用户ID
     * @apiSuccess (入参) {String} statu 标签状态(1:存在 2:已删除)
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":10,
     * "userId":1,
     * "statu":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":""
     * "data":{
     * "title": "test",
     * "name":"test",
     * "id":1
     * }
     * }
     */
    @RequestMapping(value = "/unReadList", method = RequestMethod.POST)
    @ResponseBody
    public Result unReadList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/unReadList-------------begin---------");

        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 10;
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            {
                pageNo = Integer.parseInt(map.get("pageNo").toString());
            }
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            {
                pageNum = Integer.parseInt(map.get("pageNum").toString());
            }
        }
        PageHelper.startPage(pageNo, pageNum);
        Integer userId = Integer.parseInt(map.get("userId").toString());
        Integer statu = Integer.parseInt(map.get("statu").toString());
        List<Map<String, Object>> list = notificationService.selectListByUserIdAndStatus(userId, statu);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setData(pageInfo);
        log.info("------------------/notification/unReadList-------------end---------");

        return result;
    }

    /**
     * 查看通知
     *
     * @return
     */
    /**
     * @api {POST} /notification/ReadList 03-查看通知
     * @apiVersion 1.0.0
     * @apiGroup 通知
     * @apiName 03-查看通知
     * @apiDescription 查看通知
     * @apiSuccess (入参) {Integer} notificationId 通知ID
     * @apiSuccessExample 入参示例
     * {
     * "notificationId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":""
     * "data":{
     * "notifier": "接收人名称",
     * "describe":"描述",
     * "question_description":"帖子描述",
     * "question_pic":"帖子图片",
     * "question_word":"帖子文档",
     * "question_name":"帖子名称",
     * "tag":"帖子标签名称",
     * "fileName":"文件名",
     * "question_creator":"帖子创建者"
     * }
     * }
     */
    @RequestMapping(value = "/ReadList", method = RequestMethod.POST)
    @ResponseBody
    public Result ReadList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/ReadList-------------begin---------");

        Result result = new Result();
        Integer notificationId = Integer.parseInt(map.get("notificationId").toString());
        //查看通知详情
        Map<String, Object> notificationMap = notificationService.selectByNotificationId(notificationId);
        if (notificationMap.get("comment_type").toString().equals("1")) {
            notificationMap.put("notifier", notificationMap.get("notifier").toString());
            //描述
            notificationMap.put("describe", notificationMap.get("comment_content").toString());

            //帖子描述
            if (notificationMap.get("question_description") == null || notificationMap.get("question_description").equals("")) {
                notificationMap.put("question_description", "");
            }
            //帖子照片
            if (notificationMap.get("question_pic") == null || notificationMap.get("question_pic").equals("")) {
                notificationMap.put("question_pic", "");
                notificationMap.put("question_word", "");
            }
            if (notificationMap.get("question_pic") != null && !(notificationMap.get("question_pic").equals(""))) {
                String ext = notificationMap.get("question_pic").toString().substring(notificationMap.get("question_pic").toString().lastIndexOf(".") + 1, notificationMap.get("question_pic").toString().length());
                if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") ) {
                    notificationMap.put("question_pic", notificationMap.get("question_pic").toString());
                    notificationMap.put("question_word", "");
                } else {
                    notificationMap.put("question_word", notificationMap.get("question_pic").toString());
                    notificationMap.put("question_pic", "");
                }
            }
            //帖子名称
            notificationMap.put("question_name", notificationMap.get("question_name").toString());
            //标签的名称
            Tag tag = tagService.selectById(Integer.parseInt(notificationMap.get("question_tag").toString()));
            if (tag != null && !(tag.equals(""))) {
                notificationMap.put("tag", tag.getName());
            }
            notificationMap.put("my", "");
            notificationMap.put("my_time", "");
            notificationMap.put("myCreator","");
            if (notificationMap.get("url") != null && !(notificationMap.get("url").equals(""))) {
                FileInfo fileInfo = fileInfoService.selectByFilePath(notificationMap.get("url").toString());
                if (fileInfo != null && !(fileInfo.equals(""))) {
                    notificationMap.put("fileName", fileInfo.getFileName());
                }
            }
            if (notificationMap.get("question_creator") != null && !(notificationMap.get("question_creator").equals(""))) {
                User user = userService.selectUserById(Integer.parseInt(notificationMap.get("question_creator").toString()));
                if (user != null && !(user.equals(""))) {
                    notificationMap.put("question_creator", user.getName());
                } else {
                    notificationMap.put("question_creator", "");
                }
            }
        }
        if (notificationMap.get("comment_type").toString().equals("2")) {
            //C层
            notificationMap.put("notifier", notificationMap.get("notifier").toString());
            //描述
            notificationMap.put("describe", notificationMap.get("comment_content").toString());
            Comment comment = commentService.selectByCommentId(Integer.parseInt(notificationMap.get("comment_id").toString()));
            Comment c = commentService.selectByCommentId(comment.getParentId());
            User u=userService.selectUserById(c.getCommentator());
            //B层
            notificationMap.put("my", c.getContent());
            notificationMap.put("my_time", c.getGmtCreate());
            notificationMap.put("myCreator", u.getName());
            //帖子描述
            if (notificationMap.get("question_description") == null || notificationMap.get("question_description").equals("")) {
                notificationMap.put("question_description", "");
            }
            //帖子照片
            if (notificationMap.get("question_pic") == null || notificationMap.get("question_pic").equals("")) {
                notificationMap.put("question_pic", "");
                notificationMap.put("question_word", "");
            }
            if (notificationMap.get("question_pic") != null && !(notificationMap.get("question_pic").equals(""))) {
                String ext = notificationMap.get("question_pic").toString().substring(notificationMap.get("question_pic").toString().lastIndexOf(".") + 1, notificationMap.get("question_pic").toString().length());
                if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg")) {
                    notificationMap.put("question_pic", notificationMap.get("question_pic").toString());
                    notificationMap.put("question_word", "");
                } else {
                    notificationMap.put("question_word", notificationMap.get("question_pic").toString());
                    notificationMap.put("question_pic", "");
                }
            }
            //帖子名称
            notificationMap.put("question_name", notificationMap.get("question_name").toString());
            //标签的名称
            Tag tag = tagService.selectById(Integer.parseInt(notificationMap.get("question_tag").toString()));
            notificationMap.put("tag", tag.getName());
            if (notificationMap.get("question_creator") != null && !(notificationMap.get("question_creator").equals(""))) {
                User user = userService.selectUserById(Integer.parseInt(notificationMap.get("question_creator").toString()));
                if (user != null && !(user.equals(""))) {
                    notificationMap.put("question_creator", user.getName());
                } else {
                    notificationMap.put("question_creator", "");
                }
            }
            if (notificationMap.get("url") != null && !(notificationMap.get("url").equals(""))) {
                FileInfo fileInfo = fileInfoService.selectByFilePath(notificationMap.get("url").toString());
                if (fileInfo != null && !(fileInfo.equals(""))) {
                    notificationMap.put("fileName", fileInfo.getFileName());
                }
            }
        }
        //修改通知的状态
        Notification notification = new Notification();
        notification.setStatus(1);
        notification.setId(notificationId);
        notificationService.updateNotification(notification);
        result.setCode(ResultCode.SUCCESS);
        result.setData(notificationMap);
        log.info("------------------/notification/ReadList-------------end---------");

        return result;
    }


    //@功能帖子通知(未读和已读)
    @RequestMapping(value = "/questionUnReadOrReadList", method = RequestMethod.POST)
    @ResponseBody
    public Result questionUnReadOrReadList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/questionUnReadOrReadList-------------begin---------");

        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 10;
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            {
                pageNo = Integer.parseInt(map.get("pageNo").toString());
            }
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            {
                pageNum = Integer.parseInt(map.get("pageNum").toString());
            }
        }
        PageHelper.startPage(pageNo, pageNum);
        Integer userId = Integer.parseInt(map.get("userId").toString());
        Integer statu = Integer.parseInt(map.get("statu").toString());
        List<Map<String, Object>> list = notificationService.selectQuestionUnReadOrReadListByUserIdAndStatus(userId, statu);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("questionTag") != null && !(list.get(i).get("questionTag").equals(""))) {
                    Tag tag = tagService.selectById(Integer.parseInt(list.get(i).get("questionTag").toString()));
                    if (tag != null && !(tag.equals(""))) {
                        list.get(i).put("questionTag", tag.getName());
                    }
                }
            }
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setData(pageInfo);
        log.info("------------------/notification/questionUnReadOrReadList-------------end---------");

        return result;
    }


    //@功能帖子通知(未读修改成已读)
    @RequestMapping(value = "/questionUpdateUnReadToRead", method = RequestMethod.POST)
    @ResponseBody
    public Result questionUpdateUnReadToRead(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/questionUpdateUnReadToRead-------------begin---------");

        Result result = new Result();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        notificationService.questionUpdateUnReadToRead(Integer.parseInt(map.get("id").toString()));
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/notification/questionUpdateUnReadToRead-------------end---------");

        return result;
    }

    //@功能帖子通知(未读)下拉框  以及数量
    @RequestMapping(value = "/questionUnReadOrReadListAndNum", method = RequestMethod.POST)
    @ResponseBody
    public Result questionUnReadOrReadListAndNum(@RequestBody Map<Object, Object> map) {
        log.info("------------------/notification/questionUnReadOrReadListAndNum-------------begin---------");
        Result result = new Result();
        Map<String, Object> listAndNumMap = new HashMap<>();
        Integer userId = Integer.parseInt(map.get("userId").toString());
        Integer statu = Integer.parseInt(map.get("statu").toString());
        List<Map<String, Object>> list = notificationService.selectQuestionUnReadOrReadListAndNum(userId, statu);
        if (list.size() > 0) {
            listAndNumMap.put("num", list.size());
        }
        listAndNumMap.put("list", list);
        result.setCode(ResultCode.SUCCESS);
        result.setData(listAndNumMap);
        log.info("------------------/notification/questionUnReadOrReadList-------------end---------");

        return result;
    }
}
