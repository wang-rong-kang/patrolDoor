package com.unicom.patrolDoor.entity;

public class Recommend {
    private Integer id;

    private Integer questionId;

    private String recommendTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getRecommendTime() {
        return recommendTime;
    }

    public void setRecommendTime(String recommendTime) {
        this.recommendTime = recommendTime == null ? null : recommendTime.trim();
    }
}