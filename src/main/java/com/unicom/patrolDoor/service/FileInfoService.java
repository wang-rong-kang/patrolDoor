package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.FileInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/30 14:10
 */
public interface FileInfoService {
    void insertFileInfo(FileInfo fileInfo);

    void updateIsNoKnowledgeByKnowledgeType(int isNoKnowledge, int knowledgeId);

    List<Map<String, Object>> selectAll(FileInfo fileInfo,List list);

    FileInfo selectById(Integer fileId);

    void updateFileInfo(FileInfo fileInfo);

//    void updateFileInfoByFilePath(String url);

    List<Map<String, Object>> selectAll2(FileInfo fileInfo);

    FileInfo selectByFilePath(String filePath);

    void updateFileInfoUsefulNum(int fileId);

    void updateFileInfoUnUsefulNum(int fileId);

    List<FileInfo> selectByKnowledgeIds(Integer knowledgeId);

    void updateIsNoKnowledgeByKnowledgeIds(String knowledgeIds, int fileId);

    void updateIsNoKnowledgeByFileId(int fileId);

    List<FileInfo> selectByFileStatu(FileInfo fileInfo);

    List<FileInfo> selectListByUserId(FileInfo fileInfo);

    void deleteByFilePath(String filePath);

    List<Map<String, Object>> selectAllDownload(FileInfo fileInfo);

    List<Map<Object, Object>> selectHotNewUpload();

    List<Map<Object, Object>> selectHotDownload();

    List<FileInfo> selectByKnowledgeIds2(Integer id);
}
