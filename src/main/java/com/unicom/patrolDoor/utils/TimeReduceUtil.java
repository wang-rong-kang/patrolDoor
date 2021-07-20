package com.unicom.patrolDoor.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author wrk
 * @Date 2021/6/4 10:26  时间差
 */

public class TimeReduceUtil {
    public long timeReduce(String end,String begin){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(df.format(new Date()));
        try {
            Date d1 = df.parse(end);
            Date d2 = df.parse(begin);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
//            System.out.println(minutes + "分");
            return minutes;
        } catch (Exception e) {

        }
        return  -1;
    }
}
