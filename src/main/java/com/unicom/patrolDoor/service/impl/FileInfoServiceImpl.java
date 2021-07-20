package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.FileInfoMapper;
import com.unicom.patrolDoor.entity.FileInfo;
import com.unicom.patrolDoor.service.FileInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/3/30 14:10
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {
    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    public void insertFileInfo(FileInfo fileInfo) {
        fileInfoMapper.insertSelective(fileInfo);
    }

    @Override
    public void updateIsNoKnowledgeByKnowledgeType(int isNoKnowledge, int knowledgeId) {
        fileInfoMapper.updateIsNoKnowledgeByKnowledgeType(isNoKnowledge, knowledgeId);
    }

    @Override
    public List<Map<String, Object>> selectAll(FileInfo fileInfo,List list) {
        return fileInfoMapper.selectAll(fileInfo.getFileUserId(), fileInfo.getIsNoKnowledge(), list, fileInfo.getFileBrief(), fileInfo.getFileName(),fileInfo.getFileDescribe(),fileInfo.getFileKeyWord(),fileInfo.getFileState());
    }

    @Override
    public FileInfo selectById(Integer fileId) {
        return fileInfoMapper.selectById(fileId);
    }

    @Override
    public void updateFileInfo(FileInfo fileInfo) {
        fileInfoMapper.updateFile(fileInfo);
    }

//    @Override
//    public void updateFileInfoByFilePath(String url) {
//        fileInfoMapper.updateFileInfoByFilePath(url);
//    }

    @Override
    public List<Map<String, Object>> selectAll2(FileInfo fileInfo) {
        return fileInfoMapper.selectAll2(fileInfo.getFileUserId(), fileInfo.getFileBrief(), fileInfo.getFileName(),fileInfo.getFileKeyWord(),fileInfo.getFileDescribe());
    }

    @Override
    public FileInfo selectByFilePath(String filePath) {
        return fileInfoMapper.selectByFilePath(filePath);
    }

    @Override
    public void updateFileInfoUsefulNum(int fileId) {
        fileInfoMapper.updateFileInfoUsefulNum(fileId);
    }

    @Override
    public void updateFileInfoUnUsefulNum(int fileId) {
        fileInfoMapper.updateFileInfoUnUsefulNum(fileId);
    }

    @Override
    public List<FileInfo> selectByKnowledgeIds(Integer knowledgeId) {
        return fileInfoMapper.selectByKnowledgeIds(knowledgeId);
    }

    @Override
    public void updateIsNoKnowledgeByKnowledgeIds(String knowledgeIds, int fileId) {
        fileInfoMapper.updateIsNoKnowledgeByKnowledgeIds(knowledgeIds, fileId);
    }

    @Override
    public void updateIsNoKnowledgeByFileId(int fileId) {
        fileInfoMapper.updateIsNoKnowledgeByFileId(fileId);
    }

    @Override
    public List<FileInfo> selectByFileStatu(FileInfo fileInfo) {
        return fileInfoMapper.selectByFileStatu(fileInfo);
    }

    @Override
    public List<FileInfo> selectListByUserId(FileInfo fileInfo) {
        return fileInfoMapper.selectListByUserId(fileInfo);
    }

    @Override
    public void deleteByFilePath(String filePath) {
        fileInfoMapper.deleteByFilePath(filePath);
    }

    @Override
    public List<Map<String, Object>> selectAllDownload(FileInfo fileInfo) {
        return  fileInfoMapper.selectAllDownload(fileInfo);
    }

    @Override
    public List<Map<Object, Object>> selectHotNewUpload() {
        return fileInfoMapper.selectHotNewUpload();
    }

    @Override
    public List<Map<Object, Object>> selectHotDownload() {
        return fileInfoMapper.selectHotDownload();
    }

    @Override
    public List<FileInfo> selectByKnowledgeIds2(Integer id) {
        return fileInfoMapper.selectByKnowledgeIds2(id);
    }

}
