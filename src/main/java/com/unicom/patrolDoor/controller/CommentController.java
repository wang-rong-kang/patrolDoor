package com.unicom.patrolDoor.controller;

import com.unicom.patrolDoor.entity.Comment;
import com.unicom.patrolDoor.service.CommentService;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import com.unicom.patrolDoor.utils.SensitivewordFilter;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @Author wrk
 * @Date 2021/3/24 10:38  回帖
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/comment")
public class CommentController {
    private static final Logger log = LogManager.getLogger(CommentController.class);

    @Resource
    private CommentService comentService;

    @Value(value = "${file.sensitiveTxt}")
    private String sensitiveTxt;
    /**
     * @api {GET} /comment/add 01-添加回帖
     * @apiVersion 1.0.0
     * @apiGroup 回帖
     * @apiName 01-添加回帖
     * @apiDescription 添加回帖
     * @apiSuccess (入参) {Integer} parentId 帖子的Id
     * @apiSuccess (入参) {Integer} type 回帖的类型
     * @apiSuccess (入参) {Integer} commentator 回帖人的Id,也就是登陆用户的ID
     * @apiSuccess (入参) {String} gmtCreate
     * @apiSuccess (入参) {Integer} likeCount 点赞人数
     * @apiSuccess (入参) {String} content 回帖的内容
     * @apiSuccess (入参) {Integer} commentCount 被评论多少次
     * @apiSuccess (入参) {Integer} questionId 帖子ID
     * @apiSuccessExample 入参示例
     * {
     * "parentId":1,
     * "type":2,
     * "commentator":1,
     * "likeCount":100,
     * "content":"非常喜欢",
     * "commentCount":10,
     * "questionId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"回复成功，已通知用户"
     * "data":{}
     * }
     */
    @GetMapping(value = "/add")
    public Result add(@Param("parentId") Integer parentId,
                      @Param("type") Integer type,
                      @Param("commentator") Integer commentator,
                      @Param("likeCount") Integer likeCount,
                      @Param("content") String content,
                      @Param("commentCount") Integer commentCount,
                      @Param("questionId") Integer questionId) throws Exception {
        log.info("------------------/comment/add-------------begin---------");
        Result result = new Result();
        Comment comment = new Comment();
        SensitivewordFilter sensitivewordFilter=new SensitivewordFilter();
        if (content != null && !(content.equals(""))) {
            String response = sensitivewordFilter.replaceSensitiveWord(content, 1, "*",sensitiveTxt);
            if (response.equals("false")) {
                comment.setContent(content);
            } else {
                result.setMessage("添加失败：您输入的信息包括敏感词语：" + response + ",请您修改后提交");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        comment.setParentId(parentId);
        comment.setType(type);
        comment.setCommentator(commentator);
        comment.setLikeCount(likeCount);
        comment.setCommentCount(commentCount);
        comment.setQuestion(questionId);
        result = comentService.save(comment);
        log.info("------------------/comment/add-------------end---------");
        return result;
    }
}
