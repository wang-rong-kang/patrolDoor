package com.unicom.patrolDoor.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.FileInfo;
import com.unicom.patrolDoor.entity.Knowledge;
import com.unicom.patrolDoor.entity.KnowledgeLog;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.service.FileInfoService;
import com.unicom.patrolDoor.service.KnowledgeLogService;
import com.unicom.patrolDoor.service.KnowledgeService;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/4/27 15:45
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    private static final Logger log = LogManager.getLogger(KnowledgeController.class);
    @Resource
    private KnowledgeService knowledgeService;
    @Resource
    private FileInfoService fileInfoService;

    @Value(value = "${file.serverIp}")
    private String serverIp;

    @Resource
    private UserService userService;

    @Resource
    private KnowledgeLogService knowledgeLogService;

    /**
     * @api {Post} /knowledge/list 01-知识库标签列表  未删除
     * @apiVersion 1.0.0
     * @apiGroup 知识库标签
     * @apiName 01-知识库标签列表
     * @apiDescription 知识库标签列表
     * @apiSuccess (入参) {Integer} pageNo 当前页数
     * @apiSuccess (入参) {Integer} pageNum 每页多少条
     * @apiSuccess (入参) {String} name 标签名称
     * @apiSuccess (入参) {String} statu 标签状态(1:存在 2:已删除)
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":10,
     * "name":"test",
     * "statu":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "notifier":"测试",
     * "knowledgeName":"标签1",
     * "knowledgeCreateTime":"2021-04-29 09:24:36",
     * "knowledgeUpdateTime":"2021-04-29 14:57:00",
     * "knowledgeDescribe":"test",
     * "knowledgeStatu":"1"
     * }
     */
    @PostMapping("/list")
    @ResponseBody
    public Result list(@RequestBody Map<String, Object> map) {
        log.info("------------------/knowledge/list-------------begin---------");
        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        Integer statu = -1;
        User user = new User();
        List knowledgeList = new ArrayList<>();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString();
        }
        if (map.get("statu") != null && !(map.get("statu").equals(""))) {
            statu = Integer.parseInt(map.get("statu").toString());
        }
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("id").toString()));
        }
        PageHelper.startPage(pageNo, pageNum);
        List<Knowledge> list = new ArrayList<>();
//        List<Map<Object, Object>> mapList = new ArrayList<>();
        if (user.getIsNo() == 1) {
            list = knowledgeService.selectAll(name, statu);
            PageInfo pageInfo = new PageInfo<>(list);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查询成功");
            result.setData(pageInfo);
        }
        if (user.getIsNo() == 3) {
            String[] strings = user.getTagIds().split(",");
            for (int i = 0; i < strings.length; i++) {
                knowledgeList.add(Integer.parseInt(strings[i].trim()));
            }
            list = knowledgeService.selectByNameAndIds(knowledgeList, name, statu);
            PageInfo pageInfo = new PageInfo<>(list);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查询成功");
            result.setData(pageInfo);
        }


        log.info("------------------/knowledge/list-------------end---------");

        return result;
    }

    //已删除列表
    @PostMapping("/deleteList")
    @ResponseBody
    public Result deleteList(@RequestBody Map<String, Object> map) {
        log.info("------------------/knowledge/deleteList-------------begin---------");
        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        List<Knowledge> list = new ArrayList<>();
        List<KnowledgeLog> knowledgeLogList = new ArrayList<>();
        User user = new User();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString();
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString().trim()));
        }
        PageHelper.startPage(pageNo, pageNum);

        if (user.getIsNo() == 1) {
            list = knowledgeService.selectAllDelete(name);
            PageInfo<Knowledge> pageInfo = new PageInfo<>(list);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查询成功");//超级管理员
            result.setData(pageInfo);
        } else if (user.getIsNo() == 3) {
            knowledgeLogList = knowledgeLogService.selectByUserIdAndKnowledgeName(user.getId(), name);
            PageInfo<KnowledgeLog> pageInfo = new PageInfo<>(knowledgeLogList);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查询成功");//超级管理员
            result.setData(pageInfo);
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查询失败");
            return result;
        }

        log.info("------------------/knowledge/deleteList-------------end---------");

        return result;
    }

    /**
     * @api {Post} /knowledge/labelExists 02-未删除的知识库标签(下拉框用的)
     * @apiVersion 1.0.0
     * @apiGroup 知识库标签
     * @apiName 02-未删除的知识库标签
     * @apiDescription 未删除的知识库标签
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "id":标签1,
     * "knowledgeName":"标签1",
     * "knowledgeCreateTime":"2021-04-29 09:24:36",
     * "knowledgeUpdateTime":"2021-04-29 14:57:00",
     * "knowledgeDescribe":"test",
     * "knowledgeStatu":"1"
     * }
     */
    //未删除的知识库标签(下拉框用的)
    @PostMapping("/labelExists")
    @ResponseBody
    public Result labelExistsList() {
        log.info("------------------/knowledge/labelExists-------------begin---------");

        Result result = new Result();
        List<Knowledge> list = knowledgeService.selectAllByKnowledgeStatu(1);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(list);
        log.info("------------------/knowledge/labelExists-------------end---------");

        return result;
    }

    /**
     * @api {Post} /knowledge/select 03-查看知识库标签
     * @apiVersion 1.0.0
     * @apiGroup 知识库标签
     * @apiName 03-查看知识库标签
     * @apiDescription 查看知识库标签
     * @apiSuccess (入参) {Integer} id 知识库标签ID
     * @apiSuccessExample 入参示例
     * {
     * "id":2
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "id":标签1,
     * "knowledgeName":"标签1",
     * "knowledgeCreateTime":"2021-04-29 09:24:36",
     * "knowledgeUpdateTime":"2021-04-29 14:57:00",
     * "knowledgeDescribe":"test",
     * "knowledgeStatu":"1"
     * }
     */
    @PostMapping("/select")
    @ResponseBody
    public Result select(@RequestBody Map<String, Object> map) {
        log.info("------------------/knowledge/select-------------begin---------");

        Result result = new Result();
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(map.get("id").toString()));
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查看成功");
            result.setData(knowledge);
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查询失败");
        }
        log.info("------------------/knowledge/select-------------end---------");

        return result;
    }

    /**
     * @api {Post} /knowledge/addOrDeleteOrUpdate 04-添加、删除、修改知识库标签
     * @apiVersion 1.0.0
     * @apiGroup 知识库标签
     * @apiName 04-添加、删除、修改知识库标签
     * @apiDescription 添加、删除、修改知识库标签
     * @apiSuccess (入参) {String} knowledgeName 标签名称
     * @apiSuccess (入参) {String} knowledgeDescribe 标签描述
     * @apiSuccess (入参) {Integer} knowledgeStatu 标签状态
     * @apiSuccessExample 入参示例
     * {
     * "knowledgeName":"test",
     * "knowledgeDescribe":"test",
     * "statu":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"成功"
     * "data":{}
     */
    //添加删除以及修改
    @PostMapping("/addOrDeleteOrUpdate")
    @ResponseBody
    public Result addOrUpdateOrDelete(@RequestBody Map<String, Object> map) {
        log.info("------------------/knowledge/addOrDeleteOrUpdate-------------begin---------");

        Result result = new Result();
        Knowledge knowledge = new Knowledge();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            knowledge.setId(Integer.parseInt(map.get("id").toString()));
        }
        if (map.get("knowledgeName") != null && !(map.get("knowledgeName").equals(""))) {
            knowledge.setKnowledgeName(map.get("knowledgeName").toString());
        }
        if (map.get("knowledgeDescribe") != null) {
            knowledge.setKnowledgeDescribe(map.get("knowledgeDescribe").toString());
        }
        if (map.get("knowledgeStatu") != null && !(map.get("knowledgeStatu").equals(""))) {
            knowledge.setKnowledgeStatu(Integer.parseInt(map.get("knowledgeStatu").toString()));
        }
        if (knowledge.getId() != null && !(knowledge.getId().equals(""))) {
            //修改
            knowledge.setKnowledgeUpdateTime(time);
            List<Knowledge> list = knowledgeService.selectByName(knowledge.getKnowledgeName(), knowledge.getId());
            if (list.size() > 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
            try {
                User u = new User();
                if (map.get("userId") != null && !(map.get("userId").equals(""))) {
                    u = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
                }
                if (u.getIsNo() != 1) {
                    knowledge.setKnowledgeStatu(1);//1：超级管理员  只有超级管理员删除标签  标签表里的状态才会改变

                }
                knowledgeService.update(knowledge);
                if (Integer.parseInt(map.get("knowledgeStatu").toString()) == 2 && u.getIsNo() == 1) {
                    //代表上面的修改操作是删除  此时将帖子中附件的知识库标签改成未选择分类-----超级管理员
                    try {
                        List<FileInfo> fileInfoList = fileInfoService.selectByKnowledgeIds2(Integer.parseInt(map.get("id").toString()));
                        if (fileInfoList.size() > 0) {
                            for (FileInfo fileInfo : fileInfoList) {
                                String[] strings = fileInfo.getKnowledgeIds().split(",");
                                List<String> arrayList = new ArrayList<>();
                                if (strings.length > 0) {
                                    for (int i = 0; i < strings.length; i++) {
                                        arrayList.add(strings[i].trim());
                                    }
                                    if (arrayList.contains(map.get("id").toString())) {
                                        arrayList.remove(map.get("id").toString());
                                    }
                                    if (arrayList.size() > 0) {
                                        String knowledgeIds = String.join(",", arrayList);
                                        fileInfoService.updateIsNoKnowledgeByKnowledgeIds(knowledgeIds, fileInfo.getFileId());
                                    } else {
                                        fileInfoService.updateIsNoKnowledgeByFileId(fileInfo.getFileId());
                                    }
                                }
                            }
                        }
                        //将用户中的权限进行修改
                        List<User> userList = userService.selectTags(Integer.parseInt(map.get("id").toString().trim()));
                        if (userList.size() > 0) {
                            for (User user : userList) {
                                if (user.getTagIds() != null && !(user.getTagIds().equals(""))) {
                                    String[] strings = user.getTagIds().split(",");
                                    List<String> arrayList = new ArrayList<>();
                                    if (strings.length > 0) {
                                        for (int i = 0; i < strings.length; i++) {
                                            arrayList.add(strings[i].trim());
                                        }
                                        if (arrayList.contains(map.get("id").toString())) {
                                            arrayList.remove(map.get("id").toString());
                                        }
                                        if (arrayList.size() > 0) {
                                            String tags = String.join(",", arrayList);
                                            userService.updateTagsOnUserId(tags, user.getId());
                                        } else {
                                            userService.updateTagsByUserId(user.getId());
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("将帖子中附件的知识库标签改成未选择分类---------失败");
                        result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                        result.setMessage("失败");
                        return result;
                    }

                }
                if (Integer.parseInt(map.get("knowledgeStatu").toString()) == 2 && u.getIsNo() == 3) {
                    //普通知识库管理员
                    String[] strings = u.getTagIds().split(",");
                    List<String> arrayList = new ArrayList<>();
                    if (strings.length > 0) {
                        for (int i = 0; i < strings.length; i++) {
                            arrayList.add(strings[i].trim());
                        }
                        if (arrayList.contains(map.get("id").toString())) {
                            arrayList.remove(map.get("id").toString());
                        }
                        if (arrayList.size() > 0) {
                            String tags = String.join(",", arrayList);
                            userService.updateTagsOnUserId(tags, u.getId());
                        } else {
                            userService.updateTagsByUserId(u.getId());
                        }
                    }
                    //同时写入日志
                    KnowledgeLog knowledgeLog = new KnowledgeLog();
                    knowledgeLog.setUserId(u.getId());
                    if (u.getIsNo() == 3) {
                        knowledgeLog.setCreateTime(time);
                        knowledgeLog.setKnowledgeId(knowledge.getId());
                        knowledgeLogService.insertKnowledgeLog(knowledgeLog);
                    }
                }
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("成功");
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("失败");
            }
        } else {
            //添加
            knowledge.setKnowledgeCreateTime(time);
            knowledge.setKnowledgeUpdateTime(time);
            List<Knowledge> list = knowledgeService.selectByName(knowledge.getKnowledgeName(), null);
            if (list.size() > 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("失败");
                return result;
            }
            try {
                knowledgeService.save(knowledge);
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("成功");
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("失败");
            }
        }
        log.info("------------------/knowledge/addOrDeleteOrUpdate-------------end---------");

        return result;
    }


    /**
     * 标签分类加文档
     *
     * @return
     */
    @PostMapping("/knowledgeListAndFileInfo")
    @ResponseBody
    public Result knowledgeListAndFileInfo() {
        log.info("------------------/knowledge/knowledgeListAndFileInfo-------------begin---------");
        Result result = new Result();
        List<Knowledge> list = knowledgeService.selectAllKnowledge();
        for (Knowledge knowledge : list) {
            List<FileInfo> fileInfoList = fileInfoService.selectByKnowledgeIds(knowledge.getId());
            for (FileInfo fileInfo : fileInfoList) {
                fileInfo.setUrl("http://" + serverIp + fileInfo.getFilePath() + fileInfo.getFileName());
            }
            knowledge.setList(fileInfoList);
        }
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(list);
        log.info("------------------/knowledge/knowledgeListAndFileInfo-------------end---------");

        return result;
    }


    /**
     * 启用删除标签
     *
     * @param map
     * @return
     */
    @PostMapping("/knowledgeRestart")
    @ResponseBody
    public Result knowledgeRestart(@RequestBody Map<String, Object> map) {
        log.info("------------------/knowledge/knowledgeRestart-------------begin---------");
        Result result = new Result();
        Knowledge knowledge = new Knowledge();
        if (map.get("id") == null || (map.get("id").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            return result;
        }
        if (map.get("knowledgeStatu") == null || (map.get("knowledgeStatu").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            return result;
        }
        if (map.get("userId") == null || (map.get("userId").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            return result;
        }
        knowledge.setId(Integer.parseInt(map.get("id").toString()));
        knowledge.setKnowledgeStatu(Integer.parseInt(map.get("knowledgeStatu").toString()));
        User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        if (user == null || user.equals("") || user.getIsNo() != 1) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            return result;
        }
        try {
            knowledgeService.update(knowledge);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("修改成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
        }
        log.info("------------------/knowledge/knowledgeRestart-------------end---------");

        return result;
    }
}
