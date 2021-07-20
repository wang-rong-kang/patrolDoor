package com.unicom.patrolDoor.entity;

import javax.persistence.Transient;

public class KnowledgeLog {
    private Integer id;

    private Integer userId;

    private Integer knowledgeId;

    private String createTime;

    @Transient
    private String knowledgeUpdateTime;

    @Transient
    private String knowledgeCreateTime;

    @Transient
    private String knowledgeName;

    @Transient
    private String knowledgeDescribe;

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

    public Integer getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Integer knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getKnowledgeUpdateTime() {
        return knowledgeUpdateTime;
    }

    public void setKnowledgeUpdateTime(String knowledgeUpdateTime) {
        this.knowledgeUpdateTime = knowledgeUpdateTime;
    }

    public String getKnowledgeCreateTime() {
        return knowledgeCreateTime;
    }

    public void setKnowledgeCreateTime(String knowledgeCreateTime) {
        this.knowledgeCreateTime = knowledgeCreateTime;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public String getKnowledgeDescribe() {
        return knowledgeDescribe;
    }

    public void setKnowledgeDescribe(String knowledgeDescribe) {
        this.knowledgeDescribe = knowledgeDescribe;
    }
}