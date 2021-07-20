package com.unicom.patrolDoor.entity;

public class VoteLog {
    private Integer id;

    private Integer voteId;

    private Integer userId;

    private String opt;

    private String voteTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVoteId() {
        return voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt == null ? null : opt.trim();
    }

    public String getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(String voteTime) {
        this.voteTime = voteTime == null ? null : voteTime.trim();
    }
}