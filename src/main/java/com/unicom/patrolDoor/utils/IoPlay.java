package com.unicom.patrolDoor.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @Author wrk
 * @Date 2021/4/29 8:59
 */

/**
 * 文件进行复制
 */
public class IoPlay {
    public static void main(String[] args) throws IOException {
        File f = new File("E:\\新建文本文档.txt");
        File f1 = new File("D:\\");
        IoPlay io = new IoPlay();
        io.copy(f,f1);
//        IoPlay.copyDir("D:\\file\\knowledgeFile_delete", "D:\\file\\file");//复制文件夹里的文件
    }
    public void copy(File f, File f1) throws IOException { //复制文件的方法！
//        if (!f1.exists()) {
//            f1.mkdirs();
//        }
        if (!f1.exists()) {//路径判断，是路径还是单个的文件
            File[] cf = f.listFiles();
            for (File fn : cf) {
                if (fn.isFile()) {
                    FileInputStream fis = new FileInputStream(fn);
                    FileOutputStream fos = new FileOutputStream(f1 + "\\" + fn.getName());
                    byte[] b = new byte[1024];
                    int i = fis.read(b);
                    while (i != -1) {
                        fos.write(b, 0, i);
                        i = fis.read(b);
                    }
                    fis.close();
                    fos.close();
                } else {
                    File fb = new File(f1 + "\\" + fn.getName());
                    fb.mkdir();
                    if (fn.listFiles() != null) {//如果有子目录递归复制子目录！
                        copy(fn, fb);
                    }
                }
            }
        } else {
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(f1 + "\\" + f.getName());
            byte[] b = new byte[1024];
            int i = fis.read(b);
            while (i != -1) {
                fos.write(b, 0, i);
                i = fis.read(b);
            }
            fis.close();
            fos.close();
        }
    }






    public static void copyDir(String formDir,String toDir){
        try {
            //创建目录的File对象
            File file_copy=new File(formDir);
            //判断如果它是不是一个目录
            if(!file_copy.isDirectory()){
                //不是目录就不复制
                return;
            }
            //创建目录的对象
            File file_paste=new File(toDir);
            //如果目录不存在
            if(!file_paste.exists()){
                file_paste.mkdir();
                //创建目录
            }
            //获取源目录下的File对象列表
            File[] files=file_copy.listFiles();
            for(File file:files){
                //遍历里面的对象
                //且拼接起来
                String strForm=formDir+File.separator+file.getName();
                System.out.println("需要复制的文件"+strForm);
                String strTo=toDir+File.separator+file.getName();
                System.out.println("粘贴下来的文件"+strTo+"\n");

                //判断是目录还是文件
                //判断是否是目录
                if(file.isDirectory()){
                    //递归复制目录方法
                    copyDir(strForm,strTo);
                }
                //判断是否是文件
                if(file.isFile()){
                    //是文件，就进行复制
                    System.out.println("正在进行复制文件"+file.getName());
                    copy(strForm,strTo);
                }
            }
        } catch (IOException e) {
            System.out.println("出现错误可能是拷贝的文件不存在");
            e.printStackTrace();
        }
    }

    //复制文件方法
    public static void copy(String sbr,String sbw) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader(sbr));
        BufferedWriter bw=new BufferedWriter(new FileWriter(sbw));
        String a=null;
        while ((a=br.readLine())!=null){
            bw.write(a);//输出
            bw.newLine();//换行
        }
        br.close();
        bw.close();
    }

}
