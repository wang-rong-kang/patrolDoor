package com.unicom.patrolDoor.dao;

import com.unicom.patrolDoor.entity.FileInfoLog;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface FileInfoLogMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(FileInfoLog record);

    int insertSelective(FileInfoLog record);

    FileInfoLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileInfoLog record);

    int updateByPrimaryKey(FileInfoLog record);

    Integer selectUploaNumByBeginTimeAndEndTime(@Param("begin") String begin, @Param("end") String end);

    Integer selectDownloadNumByBeginTimeAndEndTime(@Param("begin") String begin,@Param("end")  String end);

}