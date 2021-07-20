package com.unicom.patrolDoor.utils;

import java.io.File;

/**
 * @Author wrk
 * @Date 2021/6/1 10:14
 */
public class DeleatUtil {
    public static void main(String[] args) {
        //获得这个文件
        File file = new File("c:\\temp");
        //调用删除文件方法
        deleat(file);
    }

    public static void deleat(File file) {
        //检查文件是否存在，如果不存在直接返回，不进行下面的操作
        if (!file.exists()) {
            return;
        }
        //如果是文件删除，就删除文件，然后返回，不进行下面的操作
        if (file.isFile()) {
            file.delete();
            return;
        }
        //是文件夹
        if (file.isDirectory()) {
            //循环所有文件夹里面的内容并删除
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    //使用迭代，调用自己
                    deleat(f);
                }
            }
            //删除自己
            file.delete();
        }
    }
}
