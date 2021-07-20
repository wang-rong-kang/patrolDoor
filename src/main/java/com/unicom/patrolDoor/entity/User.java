package com.unicom.patrolDoor.entity;

import javax.persistence.Transient;

public class User {
    private Integer id;

    private String name;

    private String gmtCreate;

    private String gmtModified;

    private Integer onLine;//是否在线

    private Integer isNo;//是否是管理员   0: 不是  1:  超级管理员 2:论坛模块管理员 3:知识库模块管理员

    private String number;//账号

    private String password;//用户密码

    private Integer statu;//用户状态

    private String headFile;//头像

    private String email;//邮箱

    private String tagIds;//管理员管理的标签

    private Integer log;//管理员管理的标签

    private Integer isLocal;//用来判断是论坛用户还是智慧门户登陆的

    private String token;//智慧门户用户的token

    private String provinceCode;//省份编码

    @Transient
    private String loginTime;//登录时间

    @Transient
    private String lastLoginTime;//最后一次登录时间

    public Integer getOnLine() {
        return onLine;
    }

    public void setOnLine(Integer onLine) {
        this.onLine = onLine;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsNo() {
        return isNo;
    }

    public void setIsNo(Integer isNo) {
        this.isNo = isNo;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatu() {
        return statu;
    }

    public void setStatu(Integer statu) {
        this.statu = statu;
    }

    public String getHeadFile() {
        return headFile;
    }

    public void setHeadFile(String headFile) {
        this.headFile = headFile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getLog() {
        return log;
    }

    public void setLog(Integer log) {
        this.log = log;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Integer getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(Integer isLocal) {
        this.isLocal = isLocal;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}