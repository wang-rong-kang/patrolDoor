package com.unicom.patrolDoor.entity;

/**
 * @Author wrk
 * @Date 2021/5/28 11:01
 */
public class UserLog {
    private Integer id;

    private Integer userId;

    private String createTime;

    private Integer isNo;

    private String outTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getIsNo() {
        return isNo;
    }

    public void setIsNo(Integer isNo) {
        this.isNo = isNo;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}
