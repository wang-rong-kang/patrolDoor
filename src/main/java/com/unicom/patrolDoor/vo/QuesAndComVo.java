package com.unicom.patrolDoor.vo;

/**
 * @Author wrk
 * @Date 2021/3/24 17:47
 */
public class QuesAndComVo {
    private Integer questionId;//帖子的Id

    private Integer commentId;//用户的Id

    private Integer type;//类型 1:代表帖子 2: 代表回帖

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
