package com.unicom.patrolDoor.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.Question;
import com.unicom.patrolDoor.entity.Tag;
import com.unicom.patrolDoor.entity.TagLog;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.service.QuestionService;
import com.unicom.patrolDoor.service.TagLogSerive;
import com.unicom.patrolDoor.service.TagService;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import com.unicom.patrolDoor.utils.SensitivewordFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/4/8 10:26
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/tag")
public class TagController {
    private static final Logger log = LogManager.getLogger(TagController.class);

    @Resource
    private TagService tagService;

    @Value(value = "${file.sensitiveTxt}")
    private String sensitiveTxt;

    @Resource
    private UserService userService;

    @Resource
    private TagLogSerive tagLogSerive;

    @Resource
    private QuestionService questionService;

    /**
     * @api {Post} /tag/add 01-添加帖子标签
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 01-添加帖子标签
     * @apiDescription 添加帖子标签
     * @apiSuccess (入参) {String} name  帖子标签名称
     * @apiSuccessExample 入参示例
     * {
     * "name":"test"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{}
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<Object, Object> map) throws Exception {
        log.info("------------------/tag/add-------------begin---------");
        Result result = new Result();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        List<Tag> list = tagService.selectByName(map.get("name").toString(), null);
        if (list.size() > 0) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("标签已经存在");
            return result;
        }
        Tag tag = new Tag();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//??????
        tag.setGmtCreate(df.format(new Date()));
        tag.setGmtModified(df.format(new Date()));
        if (map.get("name").toString() != null && !(map.get("name").toString().equals(""))) {
            SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
            String response = sensitivewordFilter.replaceSensitiveWord(map.get("name").toString(), 1, "*", sensitiveTxt);
            if (response.equals("false")) {
                tag.setName(map.get("name").toString());
            } else {
                result.setMessage("添加失败：标签名称包括敏感词语：" + response + ",请您修改后提交");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        try {
            tagService.save(tag);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("失败");
        }
        log.info("------------------/tag/add-------------end---------");

        return result;
    }

    /**
     * @api {Post} /tag/delete 02-删除帖子标签
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 02-删除帖子标签
     * @apiDescription 删除帖子标签
     * @apiSuccess (入参) {Integer} id  帖子标签ID
     * @apiSuccessExample 入参示例
     * {
     * "id":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result delete(@RequestBody Map<Object, Object> map) {
        log.info("------------------/tag/delete-------------begin---------");
        Result result = new Result();
        User u = new User();
        List<String> arrayList = new ArrayList<>();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        Tag tag = new Tag();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//??????
        tag.setId(Integer.parseInt(map.get("id").toString()));
        String time = df.format(new Date());
        tag.setGmtModified(time);
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            u = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
            if (u.getIsNo() == 1) {
                tag.setStatu(1);//超级管理员
            }
        }
        try {
            tagService.update(tag);
            //将用户的论坛管理修改
            if (u.getIsNo() == 1) {
                List<User> userList = userService.selectTagss(Integer.parseInt(map.get("id").toString()));
                if (userList.size() > 0) {
                    for (User user : userList) {
                        if (user.getTagIds() != null && !(user.getTagIds().equals(""))) {
                            String[] strings = user.getTagIds().split(",");
                            if (strings.length > 0) {
                                for (int i = 0; i < strings.length; i++) {
                                    arrayList.add(strings[i].trim());
                                }
                                if (arrayList.contains(map.get("id").toString())) {
                                    arrayList.remove(map.get("id").toString());
                                }
                                if (arrayList.size() > 0) {
                                    String tags = String.join(",", arrayList);
                                    userService.updateTagsById(tags, user.getId());
                                } else {
                                    userService.updateTagsOnId(user.getId());
                                }
                            }
                        }
                    }
                }
                //将论坛中的帖子全部修改成未分类
                List<Question> questionList = questionService.selectByTagId(Integer.parseInt(map.get("id").toString()));
                if (questionList.size() > 0) {
                    questionService.updateQuestionByTagId(Integer.parseInt(map.get("id").toString()),time);
                }
            }
            if (u.getIsNo() == 2) {
                //论坛管理员
                if (u.getTagIds() != null && !(u.getTagIds().equals(""))) {
                    String[] strings = u.getTagIds().split(",");
                    if (strings.length > 0) {
                        for (int i = 0; i < strings.length; i++) {
                            arrayList.add(strings[i].trim());
                        }
                        if (arrayList.contains(map.get("id").toString())) {
                            arrayList.remove(map.get("id").toString());
                        }
                        if (arrayList.size() > 0) {
                            String tags= String.join(",", arrayList);
                            userService.updateTagsById(tags,u.getId());
                        } else {
                            userService.updateTagsOnId(u.getId());
                        }
                    }
                }
                TagLog tagLog = new TagLog();
                tagLog.setTagId(tag.getId());
                tagLog.setUserId(u.getId());
                tagLog.setCreateTime(time);
                tagLogSerive.insertTagLog(tagLog);
            }
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("失败");
        }
        log.info("------------------/tag/delete-------------end---------");

        return result;
    }

    /**
     * @api {Post} /tag/update 03-修改帖子标签
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 03-修改帖子标签
     * @apiDescription 修改帖子标签
     * @apiSuccess (入参) {Integer} id  帖子标签ID
     * @apiSuccess (入参) {String} name 帖子标签名称
     * @apiSuccessExample 入参示例
     * {
     * "id":1,
     * "name":"你好"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{}
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map<Object, Object> map) throws Exception {
        log.info("------------------/tag/update-------------begin---------");

        Result result = new Result();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("id") == null) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("name") == null) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
        String response = sensitivewordFilter.replaceSensitiveWord(map.get("name").toString(), 1, "*", sensitiveTxt);
        if (!(response.equals("false"))) {
            result.setMessage("修改失败：标签名称包括敏感词语：" + response + ",请您修改后提交");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }


        List<Tag> list = tagService.selectByName(map.get("name").toString(), Integer.parseInt(map.get("id").toString()));
        if (list.size() > 0) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("标签已经存在");
            return result;
        }
        Tag tag = new Tag();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
        tag.setGmtModified(df.format(new Date()));
        tag.setName(map.get("name").toString());
        tag.setId(Integer.parseInt(map.get("id").toString()));

        try {
            tagService.update(tag);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("失败");
        }
        log.info("------------------/tag/update-------------end---------");

        return result;
    }

    /**
     * @api {Post} /tag/delete 04-查看帖子标签
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 04-查看帖子标签
     * @apiDescription 查看帖子标签
     * @apiSuccess (入参) {Integer} id  帖子标签ID
     * @apiSuccessExample 入参示例
     * {
     * "id":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{
     * "id":1,
     * "name":"test",
     * "gmtCreate":"2021-04-08 09:39:16",
     * "gmtModified":"2021-04-09 09:07:30",
     * "statu":0(0:未删除  1:已删除)
     * }
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public Result select(@RequestBody Map<Object, Object> map) {
        log.info("------------------/tag/select-------------begin---------");

        Result result = new Result();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("id") == null) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        Tag tag = tagService.selectById(Integer.parseInt(map.get("id").toString()));
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.setData(tag);
        log.info("------------------/tag/select-------------end---------");

        return result;
    }

    /**
     * 标签  分页展示
     *
     * @param map
     * @return
     */
    /**
     * @api {Post} /tag/list 05-帖子标签列表
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 05-帖子标签列表
     * @apiDescription 帖子标签列表
     * @apiSuccess (入参) {Integer} pageNo  当前第几页
     * @apiSuccess (入参) {Integer} pageNum  每页展示多少条
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":10
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{
     * "id":1,
     * "name":"test",
     * "gmtCreate":"2021-04-08 09:39:16",
     * "gmtModified":"2021-04-09 09:07:30",
     * "statu":0(0:未删除  1:已删除)
     * }
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Result list(@RequestBody Map<Object, Object> map) {
        log.info("------------------/tag/list-------------begin---------");

        Result result = new Result();
        List<Tag> tagList = new ArrayList<>();
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        User user = new User();
        List list = new ArrayList<>();
        if (map.size() != 5) {
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
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString().trim();
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString().trim()));
        }
        PageHelper.startPage(pageNo, pageNum);
        if (user.getIsNo() == 1) {
            tagList = tagService.selectAll(Integer.parseInt(map.get("statu").toString()), name);
        }
        if (user.getIsNo() == 2) {
            String[] strings = user.getTagIds().trim().split(",");

            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    list.add(Integer.parseInt(strings[i].trim()));
                }
            }
            if (list.size() > 0) {
                tagList = tagService.selectAllByIdsAndNameAndStatu(list, Integer.parseInt(map.get("statu").toString()), name);
            }
        }
        PageInfo<Tag> pageInfo = new PageInfo<>(tagList);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.setData(pageInfo);
        log.info("------------------/tag/list-------------end---------");
        return result;
    }

    //已删除的帖子标签列表
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/tag/deleteList-------------begin---------");

        Result result = new Result();
        List<Tag> tagList = new ArrayList<>();
        List<TagLog> tagLogList = new ArrayList<>();
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        Integer statu = -1;
        User user = new User();
        List list = new ArrayList<>();
        if (map.size() != 5) {
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
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString().trim();
        }
        if (map.get("statu") != null && !(map.get("statu").equals(""))) {
            statu = Integer.parseInt(map.get("statu").toString().trim());
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString().trim()));
        }
        PageHelper.startPage(pageNo, pageNum);
        if (user.getIsNo() == 1) {
            tagList = tagService.selectAll(statu, name);
            PageInfo<Tag> pageInfo = new PageInfo<>(tagList);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
            result.setData(pageInfo);
        } else if (user.getIsNo() == 2) {
            String[] strings = user.getTagIds().trim().split(",");
            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    list.add(Integer.parseInt(strings[i].trim()));
                }
            }
            if (list.size() > 0) {
                tagLogList = tagLogSerive.selectByUserIdAndNameAndStatu(name, user.getId());
                PageInfo<TagLog> pageInfo = new PageInfo<>(tagLogList);
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("成功");
                result.setData(pageInfo);
            }
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("失败");
            return result;
        }

        log.info("------------------/tag/deleteList-------------end---------");
        return result;
    }


    /**
     * @api {Post} /tag/restart 06-启用帖子标签
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 06-启用帖子标签
     * @apiDescription 启用帖子标签
     * @apiSuccess (入参) {Integer} id  当前第几页
     * @apiSuccess (入参) {Integer} statu  每页展示多少条
     * @apiSuccessExample 入参示例
     * {
     * "id":1,
     * "statu":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{}
     */
    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    @ResponseBody
    public Result restart(@RequestBody Map<Object, Object> map) {
        log.info("------------------/tag/restart-------------begin---------");

        Result result = new Result();
        if (map.size() < 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("id") == null) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        if (map.get("statu") == null) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("请求参数错误");
            return result;
        }
        Tag tag = new Tag();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//??????
        tag.setGmtModified(df.format(new Date()));
        tag.setStatu(Integer.parseInt(map.get("statu").toString()));
        tag.setId(Integer.parseInt(map.get("id").toString()));

        try {
            tagService.update(tag);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("失败");
        }
        log.info("------------------/tag/restart-------------end---------");

        return result;
    }

    /**
     * 标签下拉框
     *
     * @return
     */
    /**
     * @api {Post} /tag/box 07-帖子标签下拉列表
     * @apiVersion 1.0.0
     * @apiGroup 帖子标签
     * @apiName 07-帖子标签下拉列表
     * @apiDescription 帖子标签下拉列表
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{
     * "id":1,
     * "name":"test",
     * "gmtCreate":"2021-04-08 09:39:16",
     * "gmtModified":"2021-04-09 09:07:30",
     * "statu":0(0:未删除  1:已删除)
     * }
     */
    @RequestMapping(value = "/box", method = RequestMethod.POST)
    public Result box() {
        log.info("------------------/tag/box-------------begin---------");

        Result result = new Result();
        List<Tag> tagList = tagService.selectAllByStatu(0);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.setData(tagList);
        log.info("------------------/tag/box-------------end---------");

        return result;
    }
}
