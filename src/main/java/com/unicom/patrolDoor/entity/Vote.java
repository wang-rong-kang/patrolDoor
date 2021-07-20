package com.unicom.patrolDoor.entity;

import javax.persistence.Transient;
import java.util.List;

public class Vote {
    private Integer id;

    private String theme;

    private String opt;

    private String subscript;

    private Integer userId;

    private String createTime;

    private String beginTime;

    private String endTime;

    private Integer voteStatus;
    @Transient
    private List list;

    @Transient
    private Integer endStop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme == null ? null : theme.trim();
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt == null ? null : opt.trim();
    }

    public String getSubscript() {
        return subscript;
    }

    public void setSubscript(String subscript) {
        this.subscript = subscript;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime == null ? null : beginTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Integer getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(Integer voteStatus) {
        this.voteStatus = voteStatus;
    }

    public Integer getEndStop() {
        return endStop;
    }

    public void setEndStop(Integer endStop) {
        this.endStop = endStop;
    }
}