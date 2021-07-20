package com.unicom.patrolDoor.entity;

import java.util.List;

public class Knowledge {
    private Integer id;

    private String knowledgeName;

    private String knowledgeCreateTime;

    private String knowledgeUpdateTime;

    private String knowledgeDescribe;

    private Integer knowledgeStatu;

    private List<FileInfo> list;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName == null ? null : knowledgeName.trim();
    }

    public String getKnowledgeCreateTime() {
        return knowledgeCreateTime;
    }

    public void setKnowledgeCreateTime(String knowledgeCreateTime) {
        this.knowledgeCreateTime = knowledgeCreateTime == null ? null : knowledgeCreateTime.trim();
    }

    public String getKnowledgeDescribe() {
        return knowledgeDescribe;
    }

    public void setKnowledgeDescribe(String knowledgeDescribe) {
        this.knowledgeDescribe = knowledgeDescribe == null ? null : knowledgeDescribe.trim();
    }

    public Integer getKnowledgeStatu() {
        return knowledgeStatu;
    }

    public void setKnowledgeStatu(Integer knowledgeStatu) {
        this.knowledgeStatu = knowledgeStatu;
    }

    public String getKnowledgeUpdateTime() {
        return knowledgeUpdateTime;
    }

    public void setKnowledgeUpdateTime(String knowledgeUpdateTime) {
        this.knowledgeUpdateTime = knowledgeUpdateTime;
    }

    public List<FileInfo> getList() {
        return list;
    }

    public void setList(List<FileInfo> list) {
        this.list = list;
    }
}