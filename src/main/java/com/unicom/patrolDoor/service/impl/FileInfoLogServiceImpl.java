package com.unicom.patrolDoor.service.impl;

import com.unicom.patrolDoor.dao.FileInfoLogMapper;
import com.unicom.patrolDoor.entity.FileInfoLog;
import com.unicom.patrolDoor.service.FileInfoLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/5/28 16:59
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileInfoLogServiceImpl implements FileInfoLogService {
    @Resource
    private FileInfoLogMapper fileInfoLogMapper;

    @Override
    public void insert(FileInfoLog fileInfoLog) {
        fileInfoLogMapper.insertSelective(fileInfoLog);
    }

    @Override
    public Integer selectUploaNumByBeginTimeAndEndTime(String begin, String end) {
        return fileInfoLogMapper.selectUploaNumByBeginTimeAndEndTime(begin,end);
    }

    @Override
    public Integer selectDownloadNumByBeginTimeAndEndTime(String begin, String end) {
        return fileInfoLogMapper.selectDownloadNumByBeginTimeAndEndTime(begin,end);
    }
}
