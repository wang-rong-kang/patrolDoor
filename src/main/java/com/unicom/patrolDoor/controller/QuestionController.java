package com.unicom.patrolDoor.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.*;
import com.unicom.patrolDoor.service.*;
import com.unicom.patrolDoor.utils.*;
import com.unicom.patrolDoor.vo.QuesAndComVo;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/3/24 15:48
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/question")
public class QuestionController {
    private static final Logger log = LogManager.getLogger(QuestionController.class);

    @Value(value = "${file.send}")
    private String send;

    @Value(value = "${file.file}")
    private String fileRoot;

    @Value(value = "${file.userHeadFile}")
    private String userHeadFile;

    @Value(value = "${file.questionFile}")
    private String questionFile;

    @Value(value = "${file.knowledgeFile}")
    private String knowledgeFile;

    @Value(value = "${file.file}")
    private String knowledgeDeleteFile;

    @Value(value = "${file.serverIp}")
    private String fileServerIp;

    @Value(value = "${file.sensitiveTxt}")
    private String sensitiveTxt;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private NotificationService notificationService;

    /**
     * @api {GET} /question/add 01-帖子列表
     * @apiVersion 1.0.1
     * @apiGroup 帖子
     * @apiName 01-帖子列表
     * @apiDescription 帖子列表
     * @apiSuccess (入参) {Integer} pageNo 当前第几页
     * @apiSuccess (入参) {Integer} pageNum 每页显示多少条
     * @apiSuccess (入参) {Integer} tag 帖子标签ID
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":10,
     * "tag":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":""
     * "data":{
     * "id":1,
     * "title":"测试",
     * "gmt_create":"2021-05-07 16:22:43",
     * "gmt_modified":"2021-05-07 16:22:43",
     * "creator":1,
     * "commentCount":100,
     * "viewCount":1000,
     * "likeCount":0,
     * "tag":1,
     * "description":"描述",
     * "type":0(0:未删除 1:已删除),
     * "userName":"用户名",
     * "headFile":"用户头像",
     * "tagName":"帖子标签名"
     * }
     * }
     */
    @GetMapping(value = "/list")
    public Result questionList(@Param("pageNo") Integer pageNo,
                               @Param("pageNum") Integer pageNum,
                               @Param("tag") Integer tag,
                               @Param("title") String title) {
        log.info("------------------/question/list-------------begin---------");
        Result result = new Result();
        if (pageNo == null || pageNo.equals("")) {
            pageNo = 1;
        }
        if (pageNum == null || pageNum.equals("")) {
            pageNum = 10;
        }
        PageHelper.startPage(pageNo, pageNum);
        //数据库查询所有的帖子 倒叙排列
        List<Question> list = questionService.selectAllQuestion(tag, title);
        for (Question q : list) {
            if (q.getHeadFile() == null || q.getHeadFile().equals("")) {
                q.setHeadFile("");
            }
        }
        PageInfo<Question> pageInfo = new PageInfo<>(list);
        result.setData(pageInfo);
        result.setMessage("success");
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/question/list-------------end---------");

        return result;
    }

    /**
     * @api {GET} /question/add 02-发帖子
     * @apiVersion 1.0.0
     * @apiGroup 帖子
     * @apiName 02-发帖子
     * @apiDescription 发帖子
     * @apiSuccess (入参) {String} title 帖子题目
     * @apiSuccess (入参) {Integer} gmtCreate
     * @apiSuccess (入参) {Integer} gmtModified
     * @apiSuccess (入参) {Integer} creator 帖子创建用户ID
     * @apiSuccess (入参) {String} tag 标签
     * @apiSuccess (入参) {Integer} sticky
     * @apiSuccess (入参) {String} description 帖子说明
     * @apiSuccessExample 入参示例
     * {
     * "title":"关于大数据",
     * "creator":1,
     * "commentCount":1,
     * "tag":"互联网",
     * "description":"如何做好大数据"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"发帖成功"
     * "data":{}
     * }
     */
    @GetMapping(value = "/add")
    public Result add(@Param("title") String title,
                      @Param("creator") Integer creator,
                      @Param("commentCount") Integer commentCount,
                      @Param("viewCount") Integer viewCount,
                      @Param("likeCount") Integer likeCount,
                      @Param("tag") Integer tag,
                      @Param("sticky") Integer sticky,
                      @Param("description") String description,
                      @Param("pic") String pic,
                      @Param("fileName") String url,
//                      @Param("knowledgeId") Integer knowledgeId,
                      @Param("knowledgeIds") String knowledgeIds,
                      @Param("isNoKnowledge") Integer isNoKnowledge,
                      @Param("fileKeyWord ") String fileKeyWord,
                      @Param("fileDescribe") String fileDescribe,
                      @Param("isNoQuestion") Integer isNoQuestion,
                      @Param("receiverList") Integer[] receiverList) throws Exception {
        log.info("------------------/question/add-------------begin---------");
        Question question = new Question();
        SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
        if (title != null && !(title.equals(""))) {
            String response = sensitivewordFilter.replaceSensitiveWord(title, 1, "*", sensitiveTxt);
            if (response.equals("false")) {
                question.setTitle(title);
            } else {
                Result result = new Result();
                result.setMessage("发帖失败：帖子标题包括敏感词语：" + response + ",请您修改后提交");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        question.setGmtCreate(time);
        question.setGmtModified(time);
        question.setCreator(creator);
        question.setCommentCount(commentCount);
        question.setViewCount(viewCount);
        question.setLikeCount(likeCount);
        question.setTag(tag);
        question.setSticky(sticky);
        question.setUrl(url);
        if (description != null && !(description.equals(""))) {
            String response = sensitivewordFilter.replaceSensitiveWord(description, 1, "*", sensitiveTxt);
            if (response.equals("false")) {
                question.setDescription(description);
            } else {
                Result result = new Result();
                result.setMessage("发帖失败：帖子说明包括敏感词语：" + response + ",请您修改后提交");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        question.setPic(pic);
        questionService.save(question);
        if (url != null && !(url.equals(""))) {
            FileInfo fileInfo = fileInfoService.selectByFilePath(url);
            fileInfo.setIsNoKnowledge(isNoKnowledge);
            if (isNoKnowledge == 0) {
//                    fileInfo.setKnowledgeId(0);
                fileInfo.setKnowledgeIds("");
            } else {
//                    fileInfo.setKnowledgeId(1);
                fileInfo.setKnowledgeIds(knowledgeIds);
            }

            if (StringUtils.isNotBlank(fileKeyWord)) {
                fileInfo.setFileKeyWord(fileKeyWord);
            }
            if (StringUtils.isNotBlank(fileDescribe)) {
                fileInfo.setFileDescribe(fileDescribe);
            }
            try {
                fileInfoService.updateFileInfo(fileInfo);
            } catch (Exception e) {
                log.info("------------------/question/add--文件绑定知识库失败-----------");
            }

        }
        //插入通知表
        Question q = questionService.selectLastQuestion();
        Notification notification = new Notification();
        if (isNoQuestion != null && !(isNoQuestion.equals(""))) {
            notification.setIsNoQuestion(isNoQuestion);
            notification.setNotifier(creator);
            notification.setGmtCreate(time);
            notification.setQuestionId(q.getId());
            if (receiverList.length > 0 && !(receiverList.equals(""))) {
                for (int i = 0; i < receiverList.length; i++) {
                    notification.setReceiver(receiverList[i]);
                    try {
                        notificationService.insert(notification);
                    } catch (Exception e) {
                        log.info("------------------/question/add-------------添加通知表失败---------");
                    }

                }
            }
        }
        log.info("------------------/question/add-------------end---------");

        return ResultGenerator.genSuccessResult();
    }


    /**
     * @api {GET} /question/delete 03-删除帖子（以及回帖）
     * @apiVersion 1.0.0
     * @apiGroup 帖子
     * @apiName 03-删除帖子（以及回帖）
     * @apiDescription 删除帖子（以及回帖）
     * @apiSuccess (入参) {Integer} questionId 帖子的Id
     * @apiSuccess (入参) {Integer} commentId  用户的Id
     * @apiSuccess (入参) {Integer} type  类型 1:帖子,2:回帖
     * @apiSuccessExample 入参示例
     * {
     * "questionId":1,
     * "commentId":1,
     * "type":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"删除帖子成功"
     * "data":{}
     * }
     */
    @GetMapping(value = "/delete")
    public Result delete(@Param("questionId") Integer questionId,
                         @Param("commentId") Integer commentId,
                         @Param("type") Integer type) {
        log.info("------------------/question/delete-------------begin---------");
        QuesAndComVo quesAndComVo = new QuesAndComVo();

        quesAndComVo.setQuestionId(questionId);
        quesAndComVo.setCommentId(commentId);
        quesAndComVo.setType(type);

        Result result = userService.deleteByUserIdAndQuestId(quesAndComVo);
        log.info("------------------/question/delete-------------end---------");

        return result;
    }

    /**
     * @api {GET} /question/select 04-根据帖子Id查看帖子详情
     * @apiVersion 1.0.0
     * @apiGroup 帖子
     * @apiName 04-根据帖子Id查看帖子详情
     * @apiDescription 根据帖子Id查看帖子详情
     * @apiSuccess (入参) {Integer} id 帖子ID
     * @apiSuccessExample 入参示例
     * {
     * "id":2
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"success"
     * "data":{
     * "id": 2,
     * "title": "test",
     * "gmtCreate": 1,
     * "gmtModified": 1,
     * "creator": 1,
     * "commentCount": 0,
     * "viewCount":"0",
     * "likeCount":"0",
     * "tag":"很好",
     * "sticky":10,
     * "description":"真的非常好"
     * }
     * }
     */
    @GetMapping(value = "/select")
    public Result select(@Param("id") Integer id) {
        log.info("------------------/question/select-------------begin---------");
        Result result = new Result();
        if (id != null && !(id.equals(""))) {
            Question question = questionService.selectById(id);
            if (question.getPic() != null && !(question.getPic().equals(""))) {
                String ext = question.getPic().substring(question.getPic().lastIndexOf(".") + 1, question.getPic().length());
                if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg")) {
                    question.setPicType(question.getPic());
                    question.setFileType("");
                } else {
                    question.setFileType(question.getPic());
                    question.setPicType("");
                }
            } else {
                question.setFileType("");
                question.setPicType("");
            }
            User user = userService.selectUserById(question.getCreator());
            question.setUserName(user.getName());
            //将帖子的浏览量每次都进行加一
            try {
                questionService.updateViewNumById(id);
            } catch (Exception e) {
                log.info("帖子浏览量加1失败");
            }
            if (question.getUrl() != null && !(question.getUrl().equals(""))) {
                FileInfo fileInfo = fileInfoService.selectByFilePath(question.getUrl());
                if (fileInfo != null && !(fileInfo.equals(""))) {
                    question.setFileName(fileInfo.getFileName());
                }
            }
            result.setData(question);
        } else {
            result.setData("查询失败，参数不能为空");
        }
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/question/select-------------end---------");

        return result;
    }

    /**
     * @api {GET} /question/selectByParentId 05-根据帖子Id查看帖子下面的评论
     * @apiVersion 1.0.0
     * @apiGroup 帖子
     * @apiName 05-根据帖子Id查看帖子下面的评论
     * @apiDescription 根据帖子Id查看帖子下面的评论
     * @apiSuccess (入参) {Integer} id 帖子ID
     * @apiSuccessExample 入参示例
     * {
     * "id":2
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"success"
     * "data":{
     * "parentId": 1,
     * "type": "1",
     * "commentator": 11,
     * "gmtModified": null,
     * "gmtCreate": null,
     * "likeCount": 6,
     * "content":"特别喜欢",
     * "commentCount":10
     * }
     * }
     */
    @GetMapping("/selectByParentId")
    public Result selectByParentId(@Param("id") Integer id, @Param("type") Integer type) {
        log.info("------------------/question/selectByParentId-------------begin---------");
        Result result = new Result();
        if (id != null && !(id.equals(""))) {
            List<Comment> commentList = commentService.selectByParentId(id, type);
            result.setData(commentList);
        } else {
            result.setData("查询失败，参数不能为空");
        }
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/question/selectByParentId-------------end---------");

        return result;
    }

    /**
     * 获取出今天发帖量  回复帖子量 以及评论量
     *
     * @return
     */
    /**
     * @api {POST} /question/sendQuestionNum 06-获取出今天发帖量  回复帖子量 以及评论量
     * @apiVersion 1.0.0
     * @apiGroup 帖子
     * @apiName 06-获取出今天发帖量  回复帖子量 以及评论量
     * @apiDescription 根据帖子Id查看帖子下面的评论
     * @apiSuccess (响应) {Integer} sendQuestionNum 查询今天的发帖量
     * @apiSuccess (响应) {Integer} replyQuestionNum 查询今天的回帖量
     * @apiSuccess (响应) {Integer} replyCommentNum 查询今天的评论量
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"success"
     * "data":{
     * "sendQuestionNum": 1,
     * "replyQuestionNum": 1,
     * "replyCommentNum": 11
     * }
     * }
     */
    @PostMapping(value = "/sendQuestionNum")
    public Result sendQuestionNum() {
        log.info("------------------/question/sendQuestionNum-------------begin---------");
        Result result = new Result();
        Map<String, Integer> map = new HashMap<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String begin = df.format(new Date()) + " 00:00:00";
        String end = df.format(new Date()) + " 24:00:00";
        try {
            Integer num = questionService.sendQuestionNum(begin, end);
            map.put("sendQuestionNum", num);
        } catch (Exception e) {
            map.put("sendQuestionNum", 0);
            log.info("查询今天的发帖量失败");
        }
        //回复帖子
        try {
            Integer replyQuestionNum = commentService.replyNum(begin, end, 1);
            map.put("replyQuestionNum", replyQuestionNum);
        } catch (Exception e) {
            map.put("replyQuestionNum", 0);
            log.info("查询今天的回帖量失败");
        }
        try {
            Integer replyCommentNum = commentService.replyNum(begin, end, 2);
            map.put("replyCommentNum", replyCommentNum);
        } catch (Exception e) {
            map.put("replyCommentNum", 0);
            log.info("查询今天的回复评论量失败");
        }
        result.setData(map);
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/question/sendQuestionNum-------------end---------");

        return result;
    }


//    /**
//     * 上传帖子文件  现在除了头像上传使用的是/user/upload接口  其余的都是使用的/file/upload
//     *
//     * @param file
//     * @return
//     */
//    @RequestMapping(value = "/upload", method = RequestMethod.POST)
//    public Result uploadFile(@Param("file") MultipartFile file) {
//        FileUploadAndDownUtils fileUploadAndDownUtils = new FileUploadAndDownUtils();
//        Result result = fileUploadAndDownUtils.uploadFile(file,send,fileRoot,questionFile,fileServerIp);
//        return result;
//    }

//    文件下载   以及判断系统环境
//    @RequestMapping(value = "/download", method = RequestMethod.POST)
//    public void download(@Param("name") String name, HttpServletResponse response) throws IOException {
//        FileUploadAndDownUtils fileUploadAndDownUtils = new FileUploadAndDownUtils();
//        if (name != null && !(name.equals(""))) {
//            //判断当前运行的环境是windows还是linux   因为linux是没有系统盘符
//            boolean flag = runningOnWindows();
//            String[] strings = name.split(fileRoot);
//            if (flag == true) {
//                if (strings.length > 0) {
//                    String n = send + fileRoot + strings[1];
//                    fileUploadAndDownUtils.downLoad(n, response);
//                }
//            } else {
//                fileUploadAndDownUtils.downLoad(fileRoot + strings[1], response);
//            }
//        }
//    }


// 判断系统环境
//    private static boolean runningOnWindows() {
//        String system = System.getProperty("os.name");
//        if (system.indexOf("Windows") >= 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 用户本身未分类的帖子
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/unclassifiedList", method = RequestMethod.POST)
    @ResponseBody
    public Result unclassifiedList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/question/select-------------begin---------");
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        Result result = new Result();
        User user = new User();
        List<Question> questionList = new ArrayList<>();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("title") != null && !(map.get("title").equals(""))) {
            name = map.get("title").toString();
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        }

        PageHelper.startPage(pageNo, pageNum);
        questionList = questionService.selectByNameAndStatu(name, user.getId());

        PageInfo pageInfo = new PageInfo<>(questionList);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.setData(pageInfo);
        log.info("------------------/question/select-------------end---------");

        return result;
    }


    /**
     * 超级管理员未分类的帖子
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/superUnclassifiedList", method = RequestMethod.POST)
    @ResponseBody
    public Result superUnclassifiedList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/question/superUnclassifiedList-------------begin---------");
        Integer pageNo = 1;
        Integer pageNum = 10;
        String name = "";
        Result result = new Result();
        User user = new User();
        List<Question> questionList = new ArrayList<>();
        if (map.get("pageNo") != null && !(map.get("name").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString();
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        }
        PageHelper.startPage(pageNo, pageNum);
        if (user.getIsNo() == 1) {
            questionList = questionService.selectByName(name);
        }
        PageInfo pageInfo = new PageInfo<>(questionList);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.setData(pageInfo);
        log.info("------------------/question/superUnclassifiedList-------------end---------");

        return result;
    }

    /***
     * 修改帖子所属标签
     * @param map
     * @return
     */
    @RequestMapping(value = "/updateTag", method = RequestMethod.POST)
    @ResponseBody
    public Result updateTag(@RequestBody Map<Object, Object> map) {
        log.info("------------------/question/updateTag-------------begin---------");
        Result result = new Result();
        if ((map.get("id") != null && !map.get("id").equals("")) && (map.get("tag") != null && !map.get("tag").equals(""))) {

            try {
                questionService.updateTagByQuestionAndTag(Integer.parseInt(map.get("id").toString()), Integer.parseInt(map.get("tag").toString()));
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("修改成功");
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("修改失败");
            }

        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
        }
        log.info("------------------/question/updateTag-------------end---------");
        return result;
    }


    /**
     * 个人论坛
     *
     * @param pageNo
     * @param pageNum
     * @param tag
     * @return
     */
    @GetMapping(value = "/userForumList")
    public Result userForumList(@Param("pageNo") Integer pageNo,
                                @Param("pageNum") Integer pageNum,
                                @Param("tag") Integer tag,
                                @Param("type") Integer type,
                                @Param("userId") Integer userId,
                                @Param("title") String title) {
        log.info("------------------/question/userForumList-------------begin---------");
        Result result = new Result();
        if (pageNo == null || pageNo.equals("")) {
            pageNo = 1;
        }
        if (pageNum == null || pageNum.equals("")) {
            pageNum = 10;
        }
        PageHelper.startPage(pageNo, pageNum);
        //数据库查询所有的帖子 倒叙排列
        List<Question> list = questionService.selectAllQuestionByTagAndTypeAndUserId(tag, type, userId, title);
        for (Question q : list) {
            if (q.getHeadFile()==null||q.getHeadFile().equals("")) {
                q.setHeadFile(null);
            }
        }
        PageInfo<Question> pageInfo = new PageInfo<>(list);
        result.setData(pageInfo);
        result.setMessage("success");
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/question/userForumList-------------end---------");

        return result;
    }
}
