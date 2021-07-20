package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface FileInfoMapper {

    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    void updateIsNoKnowledgeByKnowledgeType(int isNoKnowledge, int knowledgeId);

    FileInfo selectById(Integer fileId);

    void updateFile(FileInfo fileInfo);

//    void updateFileInfoByFilePath(String url);

    List<Map<String, Object>> selectAll(@Param("fileUserId") Integer fileUserId, @Param("isNoKnowledge") Integer isNoKnowledge, @Param("list") List<Integer> list, @Param("fileBrief") String fileBrief, @Param("fileName") String fileName,@Param("fileDescribe") String fileDescribe,@Param("fileKeyWord") String fileKeyWord,@Param("fileState") String fileState);

    List<Map<String, Object>> selectAll2(@Param("fileUserId") Integer fileUserId, @Param("fileBrief") String fileBrief, @Param("fileName") String fileName,@Param("fileKeyWord") String fileKeyWord,@Param("fileDescribe") String fileDescribe);

    FileInfo selectByFilePath(@Param("filePath") String filePath);

    void updateFileInfoUsefulNum(@Param("fileId") Integer fileId);

    void updateFileInfoUnUsefulNum(@Param("fileId") Integer fileId);

    List<FileInfo> selectByKnowledgeIds(@Param("knowledgeId")Integer knowledgeId);

    void updateIsNoKnowledgeByKnowledgeIds(@Param("knowledgeIds")String knowledgeIds,@Param("fileId") Integer fileId);

    void updateIsNoKnowledgeByFileId(int fileId);

    List<FileInfo> selectByFileStatu(FileInfo fileInfo);

    List<FileInfo> selectListByUserId(FileInfo fileInfo);

    void deleteByFilePath(@Param("filePath") String filePath);

    List<Map<String, Object>> selectAllDownload(FileInfo fileInfo);

    List<Map<Object, Object>> selectHotNewUpload();

    List<Map<Object, Object>> selectHotDownload();

    List<FileInfo> selectByKnowledgeIds2(@Param("knowledgeId") Integer knowledgeId);
}