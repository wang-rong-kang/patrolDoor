package com.unicom.patrolDoor.service;

import com.unicom.patrolDoor.entity.FileInfoLog;

import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/5/28 16:59
 */
public interface FileInfoLogService {
    void insert(FileInfoLog fileInfoLog);

    Integer selectUploaNumByBeginTimeAndEndTime(String begin, String end);

    Integer selectDownloadNumByBeginTimeAndEndTime(String begin, String end);

}
