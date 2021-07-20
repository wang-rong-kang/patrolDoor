package com.unicom.patrolDoor.entity;

public class FileInfo {
    private Integer fileId;

    private String fileName;

    private Integer fileSize;

    private String fileExt;

    private Integer fileUserId;

    private String filePath;

    private String fileState;

    private String fileCreateTime;

    private String fileBrief;

    private Integer creator;

    private Integer fileDownload;

    private Integer fileDownloadNum;

    private Integer knowledgeId;

    private Integer isNoKnowledge;

    private String fileDownloadTime;

    private Integer usefulNum;//有用

    private Integer unUsefulNum;//无用

    private String knowledgeIds;//文档关联知识库标签

    private Integer fileStatu;//文件审核状态 1:代表审核通过  0:代表未审核 2: 代表未通过

    private String fileKeyWord;//文件关键词

    private String fileDescribe;//文件描述

    private String url;//文件路径

    private String knowledgeName;//知识库标签名

    private String knowledgeDescribe;//知识库标签描述

    private String userName;//知识库标签描述

    private String pdfUrl;//预览文档的路径

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt == null ? null : fileExt.trim();
    }

    public Integer getFileUserId() {
        return fileUserId;
    }

    public void setFileUserId(Integer fileUserId) {
        this.fileUserId = fileUserId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public String getFileState() {
        return fileState;
    }

    public void setFileState(String fileState) {
        this.fileState = fileState == null ? null : fileState.trim();
    }

    public String getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(String fileCreateTime) {
        this.fileCreateTime = fileCreateTime == null ? null : fileCreateTime.trim();
    }

    public String getFileBrief() {
        return fileBrief;
    }

    public void setFileBrief(String fileBrief) {
        this.fileBrief = fileBrief == null ? null : fileBrief.trim();
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Integer knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Integer getIsNoKnowledge() {
        return isNoKnowledge;
    }

    public void setIsNoKnowledge(Integer isNoKnowledge) {
        this.isNoKnowledge = isNoKnowledge;
    }

    public String getFileDownloadTime() {
        return fileDownloadTime;
    }

    public void setFileDownloadTime(String fileDownloadTime) {
        this.fileDownloadTime = fileDownloadTime;
    }

    public Integer getUsefulNum() {
        return usefulNum;
    }

    public void setUsefulNum(Integer usefulNum) {
        this.usefulNum = usefulNum;
    }

    public Integer getUnUsefulNum() {
        return unUsefulNum;
    }

    public void setUnUsefulNum(Integer unUsefulNum) {
        this.unUsefulNum = unUsefulNum;
    }

    public String getKnowledgeIds() {
        return knowledgeIds;
    }

    public void setKnowledgeIds(String knowledgeIds) {
        this.knowledgeIds = knowledgeIds;
    }

    public Integer getFileStatu() {
        return fileStatu;
    }

    public void setFileStatu(Integer fileStatu) {
        this.fileStatu = fileStatu;
    }

    public String getFileKeyWord() {
        return fileKeyWord;
    }

    public void setFileKeyWord(String fileKeyWord) {
        this.fileKeyWord = fileKeyWord;
    }

    public String getFileDescribe() {
        return fileDescribe;
    }

    public void setFileDescribe(String fileDescribe) {
        this.fileDescribe = fileDescribe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(Integer fileDownload) {
        this.fileDownload = fileDownload;
    }

    public Integer getFileDownloadNum() {
        return fileDownloadNum;
    }

    public void setFileDownloadNum(Integer fileDownloadNum) {
        this.fileDownloadNum = fileDownloadNum;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}