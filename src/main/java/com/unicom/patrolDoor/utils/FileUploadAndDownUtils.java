package com.unicom.patrolDoor.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;

/**
 * @Author wrk
 * @Date 2021/4/6 15:45
 */
public class FileUploadAndDownUtils {
    private static final Logger log = LogManager.getLogger(FileUploadAndDownUtils.class);
    //图片上传
    public Result upload(MultipartFile file,String send,String fileRoot,String userHeadFile,String fileServerIp) {
        Result result = new Result();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String uuid = StringValueUtil.getUUID().replaceAll("-", "");
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("pdf")) {
                String userFilePath =send + fileRoot + userHeadFile;
                String filePath = userFilePath + uuid + "." + ext;

                File folder = new File(send + fileRoot + userHeadFile);
                //如果文件夹不存在则创建
                if (!folder.exists() && !folder.isDirectory()) {
                    folder.mkdirs();
                }
                //开始上传
                try {
                    FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
                    result.setCode(ResultCode.SUCCESS);
                    result.setMessage("上传成功");
                    result.setData("http://" + fileServerIp + fileRoot + userHeadFile + uuid + "." + ext);
                    return result;
                } catch (Exception e) {
                    log.info("上传失败");
                    result.setMessage("上传失败");
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    return result;
                }
            } else {
                result.setMessage("上传失败,上传文件格式错误");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }

        } else {
            result.setMessage("上传失败,上传文件不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
    }

//    //所有格式的文件都上传
//    public Result uploadFile(MultipartFile file,String send,String fileRoot,String questionFile,String fileServerIp) {
//        Result result = new Result();
//        if (file != null) {
//            String fileName = file.getOriginalFilename();
//            String uuid = StringValueUtil.getUUID().replaceAll("-", "");
//            String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//            String userFilePath = send +fileRoot + questionFile;
//            String filePath = userFilePath + uuid + "." + ext;
//            File folder = new File(send + fileRoot +questionFile);
//            //如果文件夹不存在则创建
//            if (!folder.exists() && !folder.isDirectory()) {
//                folder.mkdirs();
//            }
//            //开始上传
//            try {
//                FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
//                result.setCode(ResultCode.SUCCESS);
//                result.setMessage("上传成功");
//                result.setData("http://" + fileServerIp + fileRoot + questionFile + uuid + "." + ext);
//                return result;
//            } catch (Exception e) {
//                log.info("上传失败");
//                result.setMessage("上传失败");
//                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
//                return result;
//            }
//        } else {
//            result.setMessage("上传失败,上传文件不能为空");
//            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
//            return result;
//        }
//    }

//    //文件下载
//    public void downLoad(String name, HttpServletResponse response) throws IOException {
//        File file = new File(name);
//        response.setHeader("content-disposition", "attachment;filename="
//                + URLEncoder.encode(name, "UTF-8"));
//        //设置响应头控制浏览器下载该文件
//        InputStream inputStream = new FileInputStream(file);
//        //输入流
//        OutputStream out = response.getOutputStream();
//        //创建缓冲区
//        byte[] bytes = new byte[1024];
//        int i = 0;
//        while ((i = inputStream.read(bytes)) > 0) {
//            out.write(bytes, 0, i);
//        }
//        inputStream.close();
//        out.close();
//    }
//

}
