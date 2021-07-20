package com.unicom.patrolDoor.entity;


import javax.persistence.Transient;

/**
 * 回帖
 */
public class Comment {
    private Integer id;

    private Integer parentId;//帖子的Id

    private Integer type;// 回帖的类型

    private Integer commentator;//回帖人的Id  也就是登陆用户的ID

    private String gmtCreate;

    private Integer likeCount;

    private String content;//回帖的内容

    private Integer commentCount;

    private Integer statu;

    @Transient
    private String userName;

    @Transient
    private Integer question;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCommentator() {
        return commentator;
    }

    public void setCommentator(Integer commentator) {
        this.commentator = commentator;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getStatu() {
        return statu;
    }

    public void setStatu(Integer statu) {
        this.statu = statu;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getQuestion() {
        return question;
    }

    public void setQuestion(Integer question) {
        this.question = question;
    }
}