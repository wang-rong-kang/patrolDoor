//package com.unicom.patrolDoor.controller;
//import com.unicom.patrolDoor.utils.Result;
//import com.unicom.patrolDoor.utils.ResultCode;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.jodconverter.core.DocumentConverter;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//
///**
// * @Date: 2020/3/27 11:13
// * motto: Saying and doing are two different things.
// */
////@CrossOrigin(origins = "*", maxAge = 300)
//@RestController
//@RequestMapping("/test")
//public class PreviewController {
//
//    @Resource
//    private DocumentConverter converter; // 转换器
//    @Resource
//    private HttpServletResponse response;
//
//
//    @RequestMapping("/toPdfFile")
//    public Result toPdfFile(){
//        Result result=new Result();
//        File file = new File("D:\\file\\file\\你好呀.xls");//需要转换的文件
////        File file = new File("D:\\"+"file/knowledgeFile/2021-06-02-15-39-34/"+"新建 XLSX 工作表.xlsx");//需要转换的文件
//        try {
//            File newFile = new File("D:\\file");//转换之后文件生成的地址
//            if (!newFile.exists()) {
//                newFile.mkdirs();
//            }
//            //文件转化
//            converter.convert(file).to(new File("D:\\file\\hello4.pdf")).execute();
//
//            //            //使用response,将pdf文件以流的方式发送的前段
////            ServletOutputStream outputStream = response.getOutputStream();
////            InputStream in = new FileInputStream(new File("D:\\file\\hello.pdf"));// 读取文件
////            // copy文件
////            int i = IOUtils.copy(in, outputStream);
////            System.out.println(i);
////            outputStream.close();
////            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        result.setCode(ResultCode.SUCCESS);
//        return result;
//    }
//
//}
