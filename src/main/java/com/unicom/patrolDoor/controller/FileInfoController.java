package com.unicom.patrolDoor.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.*;
import com.unicom.patrolDoor.service.FileInfoLogService;
import com.unicom.patrolDoor.service.FileInfoService;
import com.unicom.patrolDoor.service.KnowledgeService;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.jodconverter.core.DocumentConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * @Author wrk
 * @Date 2021/3/30 10:12
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {
    private static final Logger log = LogManager.getLogger(FileInfoController.class);

    @Value(value = "${file.send}")
    private String send;

    @Value(value = "${file.file}")
    private String fileRoot;

    @Value(value = "${file.userHeadFile}")
    private String userHeadFile;

    @Value(value = "${file.questionFile}")
    private String questionFile;

    @Value(value = "${file.knowledgeFile}")
    private String knowledgeFile;

    @Value(value = "${file.knowledgeDeleteFile}")
    private String knowledgeDeleteFile;

    @Value(value = "${file.serverIp}")
    private String fileServerIp;

    @Value(value = "${file.sensitiveTxt}")
    private String sensitiveTxt;

    @Value(value = "${file.pdf}")
    private String pdf;

    @Resource
    private DocumentConverter converter; // 转换器
//
//    @Value(value = "${openOffice.port}")
//    private Integer host;
//
//    @Value(value = "${openOffice.linux}")
//    private String linux;
//
//    @Value(value = "${openOffice.windows}")
//    private String windows;
//
//    @Value(value = "${openOffice.mac}")
//    private String mac;

    @Resource
    private UserService userService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private FileInfoLogService fileInfoLogService;

    /**
     * @api {Post} /fileInfo/upload 01-上传文件 文件写入服务器
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 01-上传文件
     * @apiDescription 上传文件
     * @apiSuccess (入参) {Array} file 文件的数组
     * @apiSuccess (入参) {Integer} userId 当前登陆用户的Id
     * @apiSuccessExample 入参示例
     * {
     * "file":"1.nihao.txt",
     * "userId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"上传成功"
     * "data":{}
     * }
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Result uploadFile(@Param("file") MultipartFile[] file,
                             @Param("userId") Integer userId) throws Exception {
        log.info("------------------/fileInfo/upload-------------begin---------");
        Result result = new Result();
        FileInfo fileInfo = new FileInfo();
        if (file == null) {
            result.setMessage("上传文件不能是空");
            result.setCode(ResultCode.NOT_FOUND);
            return result;
        }
        User user = userService.selectUserById(userId);
        String fileName = file[0].getOriginalFilename();
        if (fileName != null && !(fileName.equals(""))) {
            SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
            String response = sensitivewordFilter.replaceSensitiveWord(fileName, 1, "*", sensitiveTxt);
            if (response.equals("false")) {
            } else {
                result.setMessage("上传失败：文件名包括敏感词语：" + response + ",请您修改后上传");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        // 设置固定的日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        // 得到格式化后的日期
        String format = sdf.format(new Date());

        //文件后缀
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String userFilePath = fileRoot + knowledgeFile + format + "/";
        File f = new File(send + userFilePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String filePath = userFilePath + fileName;

        try {
            FileUtils.writeByteArrayToFile(new File(send + filePath), file[0].getBytes());
        } catch (Exception e) {
            log.info("上传失败");
            result.setMessage("上传失败");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }

//        if (!(ext.equals("pdf"))) {
//            String pdfName = "";
//            StringBuffer buffer = new StringBuffer();
//            buffer.append(fileName.substring(0, fileName.lastIndexOf(".")));
//            buffer.append("-");
//            buffer.append(format);
//            buffer.append(".pdf");
//            pdfName = buffer.toString();
//            //此时进行文件转换  将word转换成pdf
//            File fileFormat = new File(send + userFilePath + fileName);//需要转换的文件
//            File newFile = new File(send + fileRoot + pdf);//转换之后文件生成的文件夹地址
////            if (!newFile.exists()) {
////                newFile.mkdirs();
////            }
//            if (!newFile.getParentFile().exists()) {
//                newFile.setWritable(true, false);//写操作需要添加
//                newFile.getParentFile().mkdirs();
//                newFile.createNewFile();
//            }
//            //文件转换
//            try {
////                OnlinePreview onlinePreview = new OnlinePreview();
////                OfficeManager officeManager = onlinePreview.getOfficeManager(getOfficeHome(), host);
////                OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
////                File file1 = onlinePreview.converterFile(fileFormat, (send + fileRoot + pdf + pdfName), converter);
//                converter.convert(fileFormat).to(new File(send + fileRoot + pdf + pdfName)).execute();
//                fileInfo.setPdfUrl(fileRoot + pdf + pdfName);
//            } catch (Exception e) {
//                log.error("-----------生成预览文件失败-----------------------");
//            }
//        } else {
//            fileInfo.setPdfUrl(userFilePath + fileName);
//        }
        try {

            fileInfo.setFileExt(ext);
            fileInfo.setFilePath(userFilePath);//使用相对路径
            fileInfo.setFileUserId(user.getId());
            fileInfo.setFileName(fileName);
            fileInfo.setFileState("1");
            fileInfo.setFileBrief("1");
            if (file[0].getSize() < 1024) {
                fileInfo.setFileSize(1);
            } else {
                if (file[0].getSize() % 1024 == 0) {
                    fileInfo.setFileSize((int) (file[0].getSize() / 1024));
                } else {
                    fileInfo.setFileSize((int) (file[0].getSize() / 1024) + 1);
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String time = df.format(new Date());
            fileInfo.setFileCreateTime(time);

            fileInfoService.insertFileInfo(fileInfo);
            //向file_info_log表中写日志
            FileInfoLog fileInfoLog = new FileInfoLog();
            fileInfoLog.setStatu(1);//1:上传  2:  下载
            fileInfoLog.setCreateTime(time);
            fileInfoLog.setUserId(userId);
            try {
                fileInfoLogService.insert(fileInfoLog);
            } catch (Exception e) {
                log.error("-----------上传时: 向file_info_log表中写日志失败---------------");
            }
        } catch (Exception e) {
            File fi = new File(filePath);//将将文件给删除
            if (fi.exists()) {
                FileUtils.forceDelete(fi);
                result.setMessage("上传失败");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("上传成功");
        Map map = new HashMap();
        map.put("allUrl", "http://" + fileServerIp + userFilePath + fileName);
        map.put("url", userFilePath);
        result.setData(map);
        log.info("------------------/fileInfo/upload-------------end---------");
        return result;
    }

    //上传后的文件-----数据库保存,
    @RequestMapping(value = "/updateUpload", method = RequestMethod.POST)
    public Result updateUpload(@Param("userId") Integer userId,
                               @Param("fileKeyWord") String fileKeyWord,
                               @Param("fileDescribe") String fileDescribe,
                               @Param("knowledgeIds") String knowledgeIds,
                               @Param("isNoKnowledge") Integer isNoKnowledge,
                               @Param("userFilePath") String userFilePath) throws Exception {
        Result result = new Result();
        FileInfo fileInfo = new FileInfo();
        User user = userService.selectUserById(userId);
        if (user == null || user.equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("创建失败");
            log.error("file/updateUpload-------user找不到----userId:" + userId);
            return result;
        }
        if (StringUtils.isBlank(userFilePath)) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("创建失败");
            log.error("file/updateUpload-------userFilePath参数为空");
            return result;

        }
        fileInfo = fileInfoService.selectByFilePath(userFilePath);
        if (fileInfo == null || fileInfo.equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("创建失败");
            log.error("file/updateUpload-------文件上传之后在数据库中找不到----文件的相对路径是（file_path）：" + userFilePath);
            return result;
        }
        if (StringUtils.isNotBlank(isNoKnowledge.toString())) {
            if (isNoKnowledge == 0) {
                fileInfo.setIsNoKnowledge(isNoKnowledge);
                fileInfo.setKnowledgeIds("");
            } else {
                fileInfo.setIsNoKnowledge(isNoKnowledge);
                fileInfo.setKnowledgeIds(knowledgeIds);
            }
        } else {
            fileInfo.setIsNoKnowledge(0);
            fileInfo.setKnowledgeIds("");
        }
        //用户如果是超级管理员  或者是知识库标签管理员
        List<String> userTagIds = new ArrayList<>();
        List<String> knowledgeIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(user.getTagIds())) {
            String[] strings = user.getTagIds().split(",");
            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    userTagIds.add(strings[i].trim());
                }
            }
        }
        if (StringUtils.isNotBlank(knowledgeIds)) {
            String[] strings = knowledgeIds.split(",");
            if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    knowledgeIdsList.add(strings[i].trim());
                }
            }
        }
        if (userTagIds.size() > 0 && knowledgeIdsList.size() > 0) {
            userTagIds.retainAll(knowledgeIdsList);//两个集合取交集，并且将交集的内容赋值给userTagIds
        }
        if ((user.getIsNo() == 1) || (user.getIsNo() == 3 && userTagIds.size() > 0 && userTagIds.size() == knowledgeIdsList.size())) {
            fileInfo.setFileStatu(1);
        } else {
            fileInfo.setFileStatu(0);
        }
        if (StringUtils.isNotBlank(fileKeyWord)) {
            fileInfo.setFileKeyWord(fileKeyWord);
        }
        if (StringUtils.isNotBlank(fileDescribe)) {
            fileInfo.setFileDescribe(fileDescribe);
        }
        try {
            fileInfoService.updateFileInfo(fileInfo);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("创建成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("创建失败");
            log.error("file/updateUpload-------修改数据库失败");
        }
        return result;
    }
    //文件列表-----(分类)

    /**
     * @api {Post} /fileInfo/list 06-分类的文件列表
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 06-分类的文件列表
     * @apiDescription 分类的文件列表
     * @apiSuccess (入参) {Integer} pageNo 当前页数
     * @apiSuccess (入参) {Integer} pageNum 每页多少条
     * @apiSuccess (入参) {Integer} knowledgeId 知识库标签ID
     * @apiSuccess (入参) {Integer} isNoKnowledge 是否关联知识库
     * @apiSuccess (入参) {Integer} userId 用户ID
     * @apiSuccess (入参) {String} fileBrief 上传或下载
     * @apiSuccess (入参) {String} fileName 文件名
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":50,
     * "knowledgeId":1,
     * "isNoKnowledge":1,
     * "userId":1,
     * "fileBrief":"1",
     * "pageNum":50,
     * "fileName":"test.txt"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "fileId":1,
     * "fileName":"折线图.txt",
     * "fileSize":5,
     * "fileExt":"txt",
     * "fileUserId":1,
     * "filePath":"file/knowledgeFile/2021-05-06-15-54-40/",
     * "fileState":"1",
     * "fileCreateTime":"2021-05-06 15:54:40",
     * "fileBrief":"1",
     * "knowledgeId":1,
     * "knowledgeName":"test",
     * "knowledgeDescribe":"test"
     * }
     * }
     */
    @PostMapping("/list")
    @ResponseBody
    public Result list(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/list-------------begin---------");
        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 50;
        FileInfo fileInfo = new FileInfo();
        List knowledgeIdsList = new ArrayList();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("knowledgeIds") != null && !(map.get("knowledgeIds").equals(""))) {
//            fileInfo.setKnowledgeIds(map.get("knowledgeIds").toString());
            String num = map.get("knowledgeIds").toString();
            String[] strings = num.split(",");
            for (int i = 0; i < strings.length; i++) {
                knowledgeIdsList.add(Integer.parseInt(strings[i].trim()));
            }
        }
        if (map.get("isNoKnowledge") != null && !(map.get("isNoKnowledge").equals(""))) {
            fileInfo.setIsNoKnowledge(Integer.parseInt(map.get("isNoKnowledge").toString()));
        }
        if (map.get("fileState") != null && !(map.get("fileState").equals(""))) {
            fileInfo.setFileState(map.get("fileState").toString());
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            fileInfo.setFileUserId(Integer.parseInt(map.get("userId").toString()));
        }
        if (map.get("fileBrief") != null && !(map.get("fileBrief").equals(""))) {
            fileInfo.setFileBrief(map.get("fileBrief").toString());//上传  1 下载2
        }
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }
        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }

        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        PageHelper.startPage(pageNo, pageNum);
        List<Map<String, Object>> list = new ArrayList<>();
        if (fileInfo.getFileBrief() != null && !(fileInfo.getFileBrief().equals(""))) {
            if (Integer.parseInt(fileInfo.getFileBrief()) == 1) {//上传
                list = fileInfoService.selectAll(fileInfo, knowledgeIdsList);
            }
            if (Integer.parseInt(fileInfo.getFileBrief()) == 2) {//下载
                list = fileInfoService.selectAllDownload(fileInfo);
            }
        } else {
            list = fileInfoService.selectAll(fileInfo, knowledgeIdsList);
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).put("url", "http://" + fileServerIp + list.get(i).get("filePath") + list.get(i).get("fileName"));
            if (list.get(i).get("fileUserId") != null && !(list.get(i).get("fileUserId").equals("")) && map.get("userId") != null && !(map.get("userId").equals(""))) {
                if (list.get(i).get("fileUserId").equals("userId")) {
                    list.get(i).put("statu", 1);//1代表是当前用户上传的文档
                }
            } else {
                list.get(i).put("statu", 0);//0代表不是当前用户上传的文档
            }
            if (list.get(i).get("pdfUrl") != null && !(list.get(i).equals(""))) {
                list.get(i).put("pdfUrl", send + list.get(i).get("pdfUrl").toString());
            } else {
                list.get(i).put("pdfUrl", "");
            }
            if (list.get(i).get("knowledgeIds") != null && !(list.get(i).get("knowledgeIds").equals(""))) {
                if (StringUtils.isNotBlank(list.get(i).get("knowledgeIds").toString())) {
                    String[] strings = list.get(i).get("knowledgeIds").toString().split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < strings.length; j++) {
                        Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim()));
                        buffer.append(knowledge.getKnowledgeName() + ",");
                    }
                    list.get(i).put("knowledgeIds", buffer.toString().substring(0, buffer.toString().length() - 1));
                }
            }

            if (list.get(i).get("fileKeyWord") == null || list.get(i).get("fileKeyWord").equals("")) {
                list.get(i).put("fileKeyWord", "");
            }
            if (list.get(i).get("fileDescribe") == null || list.get(i).get("fileDescribe").equals("")) {
                list.get(i).put("fileDescribe", "");
            }

        }
        PageInfo pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(pageInfo);
        log.info("------------------/fileInfo/list-------------end---------");

        return result;
    }

    /**
     * 知识库文件列表
     *
     * @param map
     * @return
     */
    @PostMapping("/listByAll")
    @ResponseBody
    public Result listByAll(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/listByAll-------------begin---------");
        List<Integer> knowledgeIdsList = new ArrayList<>();
        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 50;
        FileInfo fileInfo = new FileInfo();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("knowledgeIds") != null && !(map.get("knowledgeIds").equals(""))) {
//            fileInfo.setKnowledgeIds(map.get("knowledgeIds").toString());
            String num = map.get("knowledgeIds").toString();
            String[] strings = num.split(",");
            for (int i = 0; i < strings.length; i++) {
                knowledgeIdsList.add(Integer.parseInt(strings[i].trim()));
            }
        }
        if (map.get("isNoKnowledge") != null && !(map.get("isNoKnowledge").equals(""))) {
            fileInfo.setIsNoKnowledge(Integer.parseInt(map.get("isNoKnowledge").toString()));
        }
        if (map.get("fileBrief") != null && !(map.get("fileBrief").equals(""))) {
            fileInfo.setFileBrief(map.get("fileBrief").toString());
        }
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }

        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }
        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        PageHelper.startPage(pageNo, pageNum);
        List<Map<String, Object>> list = fileInfoService.selectAll(fileInfo, knowledgeIdsList);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).put("url", "http://" + fileServerIp + list.get(i).get("filePath") + list.get(i).get("fileName"));
            if (list.get(i).get("fileUserId") != null && !(list.get(i).get("fileUserId").equals("")) && map.get("userId") != null && !(map.get("userId").equals(""))) {
                if (list.get(i).get("fileUserId").toString().equals(map.get("userId").toString())) {
                    list.get(i).put("statu", 1);//1代表是当前用户上传的文档
                } else {
                    list.get(i).put("statu", 0);//0代表不是当前用户上传的文档
                }
            } else {
                list.get(i).put("statu", 0);//0代表不是当前用户上传的文档
            }
            if (list.get(i).get("knowledgeIds") != null && !(list.get(i).get("knowledgeIds").equals(""))) {
                if (StringUtils.isNotBlank(list.get(i).get("knowledgeIds").toString())) {
                    String[] strings = list.get(i).get("knowledgeIds").toString().split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < strings.length; j++) {
                        Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim().trim()));
                        buffer.append(knowledge.getKnowledgeName() + ",");
                    }
                    list.get(i).put("knowledgeIds", buffer.toString().substring(0, buffer.toString().length() - 1));
                }
            }
            if (list.get(i).get("pdfUrl") != null && !(list.get(i).equals(""))) {
                list.get(i).put("pdfUrl", send + list.get(i).get("pdfUrl").toString());
            } else {
                list.get(i).put("pdfUrl", "");
            }
            if (list.get(i).get("fileKeyWord") == null || list.get(i).get("fileKeyWord").equals("")) {
                list.get(i).put("fileKeyWord", "");
            }
            if (list.get(i).get("fileDescribe") == null || list.get(i).get("fileDescribe").equals("")) {
                list.get(i).put("fileDescribe", "");
            }
            if (list.get(i).get("usefulNum") == null || list.get(i).get("usefulNum").equals("")) {
                list.get(i).put("usefulNum", 0);
            }
            if (list.get(i).get("unUsefulNum") == null || list.get(i).get("unUsefulNum").equals("")) {
                list.get(i).put("unUsefulNum", 0);
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(pageInfo);
        log.info("------------------/fileInfo/listByAll-------------end---------");

        return result;
    }

    /**
     * @api {Post} /fileInfo/list2 07-未分类的文件列表
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 07-未分类的文件列表
     * @apiDescription 未分类的文件列表
     * @apiSuccess (入参) {Integer} pageNo 当前页数
     * @apiSuccess (入参) {Integer} pageNum 每页多少条
     * @apiSuccess (入参) {Integer} userId 用户ID
     * @apiSuccess (入参) {String} fileBrief 上传或下载
     * @apiSuccess (入参) {String} fileName 文件名
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":50,
     * "knowledgeId":1,
     * "isNoKnowledge":1,
     * "userId":1,
     * "fileBrief":"1",
     * "pageNum":50,
     * "fileName":"test.txt"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "fileId":1,
     * "fileName":"折线图.txt",
     * "fileSize":5,
     * "fileExt":"txt",
     * "fileUserId":1,
     * "filePath":"file/knowledgeFile/2021-05-06-15-54-40/",
     * "fileState":"1",
     * "fileCreateTime":"2021-05-06 15:54:40",
     * "fileBrief":"1",
     * "knowledgeId":1,
     * "knowledgeName":"test",
     * "knowledgeDescribe":"test"
     * }
     * }
     */
    @PostMapping("/list2")
    @ResponseBody
    public Result list2(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/list2-------------begin---------");

        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 50;
        FileInfo fileInfo = new FileInfo();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            fileInfo.setFileUserId(Integer.parseInt(map.get("userId").toString()));
        }
        if (map.get("fileBrief") != null && !(map.get("fileBrief").equals(""))) {
            fileInfo.setFileBrief(map.get("fileBrief").toString());
        }
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }
        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }
        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        PageHelper.startPage(pageNo, pageNum);
        List<Map<String, Object>> list = fileInfoService.selectAll2(fileInfo);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).put("url", "http://" + fileServerIp + list.get(i).get("filePath") + list.get(i).get("fileName"));
            if (list.get(i).get("knowledgeIds") != null && !(list.get(i).get("knowledgeIds").equals(""))) {
                if (StringUtils.isNotBlank(list.get(i).get("knowledgeIds").toString())) {
                    String[] strings = list.get(i).get("knowledgeIds").toString().split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < strings.length; j++) {
                        Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim()));
                        buffer.append(knowledge.getKnowledgeName() + ",");
                    }
                    list.get(i).put("knowledgeIds", buffer.toString().substring(0, buffer.toString().length() - 1));
                }
            }
            if (list.get(i).get("pdfUrl") != null && !(list.get(i).equals(""))) {
                list.get(i).put("pdfUrl", send + list.get(i).get("pdfUrl").toString());
            } else {
                list.get(i).put("pdfUrl", "");
            }
            if (list.get(i).get("fileKeyWord") == null || list.get(i).get("fileKeyWord").equals("")) {
                list.get(i).put("fileKeyWord", "");
            }
            if (list.get(i).get("fileDescribe") == null || list.get(i).get("fileDescribe").equals("")) {
                list.get(i).put("fileDescribe", "");
            }

        }
        PageInfo pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(pageInfo);
        log.info("------------------/fileInfo/list2-------------end---------");

        return result;
    }


    //文件查看

    /**
     * @api {Post} /fileInfo/select 05-查看文件
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 05-查看文件
     * @apiDescription 查看文件
     * @apiSuccess (入参) {Integer} id 文件的ID
     * @apiSuccessExample 入参示例
     * {
     * "id":1
     * }
     * @apiSuccess (响应) {Integer} fileId 文件ID
     * @apiSuccess (响应) {String} fileName 文件名
     * @apiSuccess (响应) {Integer} fileSize 文件大小(单位:KB)
     * @apiSuccess (响应) {String} fileExt 文件后缀
     * @apiSuccess (响应) {Integer} fileUserId 文件上传用户ID
     * @apiSuccess (响应) {String} filePath 文件相对路径
     * @apiSuccess (响应) {String} fileState 文件状态(1:存在  0:删除)
     * @apiSuccess (响应) {String} fileCreateTime 文件创建时间
     * @apiSuccess (响应) {String} fileBrief 文件上传或下载(1:上传 2:下载)
     * @apiSuccess (响应) {Integer} knowledgeId 知识库标签ID
     * @apiSuccess (响应) {Integer} isNoKnowledge 文件是否关联知识库(1:是 0:不是)
     * @apiSuccess (响应) {String} fileDownloadTime 文件下载时间
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"查看成功"
     * "data":{
     * "fileId":1,
     * "fileName":"折线图.txt",
     * "fileSize":5,
     * "fileExt":"txt",
     * "fileUserId":1,
     * "filePath":"file/knowledgeFile/2021-05-06-15-54-40/",
     * "fileState":"1",
     * "fileCreateTime":"2021-05-06 15:54:40",
     * "fileBrief":"1",
     * "knowledgeId":1,
     * "isNoKnowledge":1,
     * "fileDownloadTime":"2021-05-07 16:39:36"
     * }
     * }
     */
    @PostMapping("/select")
    @ResponseBody
    public Result select(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/select-------------begin---------");
        Result result = new Result();
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            FileInfo fileInfo = fileInfoService.selectById(Integer.parseInt(map.get("id").toString()));
            User user = userService.selectUserById(fileInfo.getFileUserId());
            fileInfo.setUserName(user.getName());
            if (StringUtils.isNotBlank(fileInfo.getFilePath())) {
                fileInfo.setUrl("http://" + fileServerIp + fileInfo.getFilePath() + fileInfo.getFileName());
            }
            if (StringUtils.isNotBlank(fileInfo.getKnowledgeIds())) {
                String[] strings = fileInfo.getKnowledgeIds().split(",");
                StringBuffer buffer = new StringBuffer();
                for (int j = 0; j < strings.length; j++) {
                    Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim()));
                    buffer.append(knowledge.getKnowledgeName() + ",");
                }
                fileInfo.setKnowledgeName(buffer.toString().substring(0, buffer.toString().length() - 1));
            }
            if (fileInfo.getPdfUrl() != null && !(fileInfo.getPdfUrl().equals(""))) {
                fileInfo.setPdfUrl(send + fileInfo.getPdfUrl());
            }
            if (fileInfo.getFileKeyWord() == null || fileInfo.getFileKeyWord().equals("")) {
                fileInfo.setFileKeyWord("");
            }
            if (fileInfo.getFileDescribe() == null || fileInfo.getFileDescribe().equals("")) {
                fileInfo.setFileDescribe("");
            }
            if (fileInfo.getUsefulNum() == null || fileInfo.getUsefulNum().equals("")) {
                fileInfo.setUsefulNum(0);
            }
            if (fileInfo.getUnUsefulNum() == null || fileInfo.getUnUsefulNum().equals("")) {
                fileInfo.setUnUsefulNum(0);
            }
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("查询成功");
            result.setData(fileInfo);
            log.info("查询成功");
        }
        log.info("------------------/fileInfo/select-------------end---------");
        return result;
    }

    /**
     * @api {Post} /fileInfo/update 04-修改文件
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 04-修改文件
     * @apiDescription 修改文件
     * @apiSuccess (入参) {Integer} id 文件的ID
     * @apiSuccess (入参) {Integer} isNoKnowledge 是否关联知识库(0:是  1: 不是)
     * @apiSuccess (入参) {Integer} knowledgeId 知识库标签
     * @apiSuccess (入参) {String} fileName 文件名 现在不修改 字段必须有
     * @apiSuccessExample 入参示例
     * {
     * "fileId":1,
     * "isNoKnowledge":0,
     * "knowledgeId":1,
     * "fileName":""
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"修改成功"
     * "data":{}
     * }
     */
    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/update-------------begin---------");

        Result result = new Result();
        FileInfo fileInfo = new FileInfo();
        List<String> userTagIds = new ArrayList<>();
        List<String> knowledgeIdsList = new ArrayList<>();
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            fileInfo.setFileId(Integer.parseInt(map.get("id").toString()));
        }
        FileInfo info = fileInfoService.selectById(Integer.parseInt(map.get("id").toString()));
        String oldFilePath = "";
        oldFilePath = send + info.getFilePath() + info.getFileName();
        User user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }
        if (map.get("isNoKnowledge") != null && !(map.get("isNoKnowledge").equals(""))) {
            fileInfo.setIsNoKnowledge(Integer.parseInt(map.get("isNoKnowledge").toString()));
        }
        if (map.get("knowledgeIds") != null && !(map.get("knowledgeIds").equals(""))) {
            fileInfo.setKnowledgeIds(map.get("knowledgeIds").toString());
            if (StringUtils.isNotBlank(user.getTagIds())) {
                String[] strings = user.getTagIds().split(",");
                if (strings.length > 0) {
//                    List<String> strList = Arrays.asList(strings);
//                    userTagIds = new ArrayList<>(strList);
                    for (int i = 0; i < strings.length; i++) {
                        userTagIds.add(strings[i].trim());
                    }
                }
            }
            if (StringUtils.isNotBlank(map.get("knowledgeIds").toString())) {
                String[] strings = map.get("knowledgeIds").toString().split(",");
                if (strings.length > 0) {
//                    List<String> strList = Arrays.asList(strings);
//                    knowledgeIdsList = new ArrayList<>(strList);
                    for (int i = 0; i < strings.length; i++) {
                        knowledgeIdsList.add(strings[i].trim());
                    }
                }
            }
            if (userTagIds.size() > 0 && knowledgeIdsList.size() > 0) {
                userTagIds.retainAll(knowledgeIdsList);//两个集合取交集，并且将交集的内容赋值给userTagIds
            }


        }
        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }
        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        FileInfo file = new FileInfo();
        if (map.get("userFilePath") != null && !(map.get("userFilePath").equals(""))) {
            String filePath = map.get("userFilePath").toString();
            fileInfo.setFilePath(filePath);
            //根据filePath 查询新的文件
            file = fileInfoService.selectByFilePath(filePath);
            if (file == null || file.equals("")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("修改失败");
                log.info("------/fileInfo/update----文档上传成功-----数据库中没保存------");
                return result;
            }
            if (map.get("pdfUrl") != null && !(map.get("pdfUrl").equals(""))) {
                fileInfo.setPdfUrl(map.get("pdfUrl").toString());
            }
            fileInfo.setFileName(file.getFileName());
            fileInfo.setFileSize(file.getFileSize());
            fileInfo.setFileExt(file.getFileExt());
            fileInfo.setFileCreateTime(file.getFileCreateTime());
            fileInfo.setFileState(file.getFileState());
            fileInfo.setFileBrief(file.getFileBrief());
            try {
                fileInfoService.deleteByFilePath(filePath);
            } catch (Exception e) {
                log.error("----------删除失败----这里是真的删除数据库---因为新文件做了一个备份------");
            }

        }
        if ((user.getIsNo() == 1) || (user.getIsNo() == 3 && userTagIds.size() > 0 && userTagIds.size() == knowledgeIdsList.size())) {
            fileInfo.setFileStatu(1);
        } else if (user.getId().equals(info.getFileUserId())) {
            fileInfo.setFileStatu(0);
        } else {
            //编辑的文件不是自己发表的  并且当前登陆用户不是超级管理员也不是模块管理员
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            log.info("修改失败");
            return result;
        }
        try {
            fileInfoService.updateFileInfo(fileInfo);
            //修改数据库成功之后  将之前的文件删除
//            File f=new File(oldFilePath);
//            DeleatUtil.deleat(f);
            File f = new File(oldFilePath);//文件
            File f1 = new File(send + fileRoot + knowledgeDeleteFile);

            IoPlay io = new IoPlay();
            try {
                io.copy(f, f1);
                //将之前的文件进行删除
//                    FileUtils.forceDelete(f);
                DeleatUtil.deleat(f);

            } catch (IOException e) {
                log.info("文件复制失败");
            }

            if (fileInfo.getPdfUrl() != null && (fileInfo.getPdfUrl().equals(""))) {
                File pdfFile = new File(send + fileInfo.getPdfUrl());//文件
                File pdfFileDelete = new File(send + fileRoot + knowledgeDeleteFile);
                try {
                    io.copy(pdfFile, pdfFileDelete);

                    DeleatUtil.deleat(pdfFile);

                } catch (IOException e) {
                    log.info("pdf文件复制失败");
                }
            }
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("修改成功");
            log.info("修改成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("修改失败");
            log.info("修改失败");
        }
        log.info("------------------/fileInfo/update-------------begin---------");
        return result;
    }

    /**
     * 有用和无用
     *
     * @param map
     * @return
     */
    @PostMapping("/updateUsefulNumOrUnUsefulNum")
    @ResponseBody
    public Result updateUsefulNumOrUnUsefulNum(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/updateUsefulNumOrUnUsefulNum-------------begin---------");

        Result result = new Result();
        if (map.get("id") == null && (map.get("id").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("点赞失败");
            log.info("id------不能为空");
            return result;
        }
        if (map.get("onclick") == null && (map.get("onclick").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("点赞失败");
            log.info("点赞不能是空");
            return result;
        }
        if (map.get("unclick") == null && (map.get("unclick").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("差评失败");
            log.info("差评不能是空");
            return result;
        }
        Integer onclickNum = (Integer.parseInt(map.get("onclick").toString()));
        Integer unclickNum = (Integer.parseInt(map.get("unclick").toString()));
        if (onclickNum != 0) {
            try {
                fileInfoService.updateFileInfoUsefulNum(Integer.parseInt(map.get("id").toString()));
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("点赞成功");
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("点赞失败");
                log.info("修改数据库失败");
            }
        }
        if (unclickNum != 0) {
            //无用
            try {
                fileInfoService.updateFileInfoUnUsefulNum(Integer.parseInt(map.get("id").toString()));
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("差评成功");
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("差评失败");
                log.info("修改数据库失败");
            }
        }
        log.info("------------------/fileInfo/updateUsefulNumOrUnUsefulNum-------------end---------");
        return result;
    }


    //文件删除

    /**
     * @api {Post} /fileInfo/delete 03-删除文件
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 03-删除文件
     * @apiDescription 删除文件
     * @apiSuccess (入参) {Integer} fileId 文件的ID
     * @apiSuccess (入参) {Integer} userId 当前登陆用户ID
     * @apiSuccessExample 入参示例
     * {
     * "fileId":1,
     * "userId":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"删除成功"
     * "data":{}
     * }
     */
    @PostMapping("/delete")
    @ResponseBody
    public Result delete(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/delete-------------begin---------");

        Result result = new Result();
        FileInfo fileInfo = new FileInfo();
        User user = new User();
        if (map.get("fileId") != null && !(map.get("fileId").equals(""))) {
            fileInfo = fileInfoService.selectById(Integer.parseInt(map.get("fileId").toString()));
        }
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        }
        List<String> userTagIds = new ArrayList<>();
        List<String> knowledgeIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(user.getTagIds())) {
            String[] strings = user.getTagIds().split(",");
            if (strings.length > 0) {
//                List<String> strList = Arrays.asList(strings);
//                userTagIds = new ArrayList<>(strList);
                for (int i = 0; i < strings.length; i++) {
                    userTagIds.add(strings[i].trim());
                }
            }
        }
        if (StringUtils.isNotBlank(fileInfo.getKnowledgeIds())) {
            String[] strings = fileInfo.getKnowledgeIds().split(",");
            if (strings.length > 0) {
//                List<String> strList = Arrays.asList(strings);
//                knowledgeIdsList = new ArrayList<>(strList);
                for (int i = 0; i < strings.length; i++) {
                    knowledgeIdsList.add(strings[i].trim());
                }
            }
        }
        if (userTagIds.size() > 0 && knowledgeIdsList.size() > 0) {
            userTagIds.retainAll(knowledgeIdsList);//两个集合取交集，并且将交集的内容赋值给userTagIds
        }

        if ((fileInfo.getFileUserId().equals(Integer.parseInt(map.get("userId").toString()))) || (user.getIsNo() == 1) || (user.getIsNo() == 3 && userTagIds.size() > 0 && userTagIds.size() == knowledgeIdsList.size())) {
            //文档是当前用户上传的 或者是超级管理员  或者是模块管理员
            //此时是将文件从一个文件夹移入到删除的文件夹中,同时修改文件的状态
            File f = new File(send + fileInfo.getFilePath() + fileInfo.getFileName());
            File f1 = new File(send + fileRoot + knowledgeDeleteFile);
            IoPlay io = new IoPlay();
            try {
                io.copy(f, f1);
                try {
                    //将之前的文件进行删除
                    FileUtils.forceDelete(f);
                } catch (IOException e) {
                    log.info("删除旧文件失败");
                }

            } catch (IOException e) {
                log.info("文件复制失败");
            }

            if (fileInfo.getPdfUrl() != null && (fileInfo.getPdfUrl().equals(""))) {
                File pdfFile = new File(send + fileInfo.getPdfUrl());//文件
                File pdfFileDelete = new File(send + fileRoot + knowledgeDeleteFile);
                try {
                    io.copy(pdfFile, pdfFileDelete);
                    FileUtils.forceDelete(pdfFile);
                } catch (IOException e) {
                    log.info("pdf文件复制失败");
                }
            }
            fileInfo.setFileState("0");
            fileInfoService.updateFileInfo(fileInfo);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("文件删除成功");
            log.info("文件删除成功");
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("文件删除失败");
            log.info("文件删除失败");
        }
        log.info("------------------/fileInfo/delete-------------end---------");
        return result;
    }

    /**
     * @api {Post} /fileInfo/dowload 02-下载文件
     * @apiVersion 1.0.0
     * @apiGroup 文件
     * @apiName 02-下载文件
     * @apiDescription 下载文件
     * @apiSuccess (入参) {String} url 文件的相对路径
     * @apiSuccessExample 入参示例
     * {
     * "url":"file/knowledgeFile/2021-05-06-16-47-03/"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"下载成功"
     * "data":{}
     * }
     */
    //文件下载
    @PostMapping("/dowload")
    @ResponseBody
    public Result dowload(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/dowload-------------begin---------");

        Result result = new Result();
//        if (map.get("allUrl") != null && !(map.get("allUrl").equals(""))) {
//            File f = new File(map.get("allUrl").toString());
//            if (!f.exists()) {
//                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
//                result.setMessage("下载失败");
//                return result;
//            }
//        }
        //相对路径
        if (map.get("userId") == null || map.get("userId").equals("")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("下载失败");
            return result;
        }
        if (map.get("url") != null && !(map.get("url").equals(""))) {
            FileInfo fileInfo = fileInfoService.selectByFilePath(map.get("url").toString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

            try {
                String time = df.format(new Date());
//                fileInfoService.updateFileInfoByFilePath(map.get("url").toString());
                //向file_info_log表中写日志
                FileInfoLog fileInfoLog = new FileInfoLog();
                fileInfoLog.setStatu(2);//1:上传  2:  下载
                fileInfoLog.setCreateTime(time);
                fileInfoLog.setFileId(fileInfo.getFileId());
                fileInfoLog.setUserId(Integer.parseInt(map.get("userId").toString()));
                try {
                    fileInfoLogService.insert(fileInfoLog);
                } catch (Exception e) {
                    log.error("-----------下载时: 向file_info_log表中写日志失败---------------");
                }

                result.setCode(ResultCode.SUCCESS);
                result.setMessage("下载成功");
                log.info("下载成功");
                return result;
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("下载失败");
                log.info("下载失败");
                return result;
            }

        }
        log.info("------------------/fileInfo/dowload-------------end---------");
        return result;
    }


    /**
     * 管理员审核列表
     *
     * @param map
     * @return
     */
    @PostMapping("/examineList")
    @ResponseBody
    public Result examineList(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/examineList-------------begin---------");
        Integer pageNo = 1;
        Integer pageNum = 10;
        Result result = new Result();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        //普通用户  管理员都传
        if (map.get("fileStatu") == null || (map.get("fileStatu").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看列表失败");
            return result;
        }
        FileInfo fileInfo = new FileInfo();
        if (map.get("fileStatu") != null && !(map.get("fileStatu").equals(""))) {
            fileInfo.setFileStatu(Integer.parseInt(map.get("fileStatu").toString()));
        }
        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }
        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }
        PageHelper.startPage(pageNo, pageNum);
        List<FileInfo> list = fileInfoService.selectByFileStatu(fileInfo);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setUrl("http://" + fileServerIp + list.get(i).getFilePath() + list.get(i).getFileName());
                if (StringUtils.isNotBlank(list.get(i).getKnowledgeIds())) {
                    String[] strings = list.get(i).getKnowledgeIds().split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < strings.length; j++) {
                        Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim()));
                        buffer.append(knowledge.getKnowledgeName() + ",");
                    }
                    list.get(i).setKnowledgeIds(buffer.toString().substring(0, buffer.toString().length() - 1));
                }
                if (list.get(i).getPdfUrl() != null && !(list.get(i).getPdfUrl().equals(""))) {
                    list.get(i).setPdfUrl(send + list.get(i).getPdfUrl());
                }
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(pageInfo);
        log.info("------------------/fileInfo/examineList-------------end---------");
        return result;
    }


    /**
     * 审核状态
     *
     * @param map
     * @return
     */
    @PostMapping("/examine")
    @ResponseBody
    public Result examine(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/examine-------------begin---------");
        Result result = new Result();
        User user = new User();
        FileInfo fileInfo = new FileInfo();
        if (map.get("userId") != null && !(map.get("userId").equals(""))) {
            user = userService.selectUserById(Integer.parseInt(map.get("userId").toString()));
        }
        if (map.get("fileId") != null && !(map.get("fileId").equals(""))) {
            fileInfo = fileInfoService.selectById(Integer.parseInt(map.get("fileId").toString()));
        }
        if (map.get("fileStatu") != null && !(map.get("fileStatu").equals(""))) {
            fileInfo.setFileStatu(Integer.parseInt(map.get("fileStatu").toString()));
        }
        List<String> userTagIds = new ArrayList<>();
        List<String> knowledgeIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(user.getTagIds())) {
            String[] strings = user.getTagIds().split(",");
            if (strings.length > 0) {
//                List<String> strList = Arrays.asList(strings);
//                userTagIds = new ArrayList<>(strList);
                for (int i = 0; i < strings.length; i++) {
                    userTagIds.add(strings[i].trim());
                }
            }
        }
        if (StringUtils.isNotBlank(fileInfo.getKnowledgeIds())) {
            String[] strings = fileInfo.getKnowledgeIds().split(",");
            if (strings.length > 0) {
//                List<String> strList = Arrays.asList(strings);
//                knowledgeIdsList = new ArrayList<>(strList);
                for (int i = 0; i < strings.length; i++) {
                    knowledgeIdsList.add(strings[i].trim());
                }
            }
        }
        if (userTagIds.size() > 0 && knowledgeIdsList.size() > 0) {
            userTagIds.retainAll(knowledgeIdsList);//两个集合取交集，并且将交集的内容赋值给userTagIds
        }
        if ((user.getIsNo() == 1) || (user.getIsNo() == 3 && userTagIds.size() > 0 && userTagIds.size() == knowledgeIdsList.size())) {
//            fileInfo.setFileStatu(1);
            try {
                fileInfoService.updateFileInfo(fileInfo);
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("审核成功");
            } catch (Exception e) {
                log.error("/fileInfo/examine------接口---------修改数据库失败-----");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("审核失败---您权限不足");
            }

        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("审核失败---您权限不足");
        }
        log.info("------------------/fileInfo/examine-------------end---------");
        return result;
    }


    /**
     * 用户审核列表
     *
     * @param map
     * @return
     */
    @PostMapping("/listByUserId")
    @ResponseBody
    public Result listByUserId(@RequestBody Map<String, Object> map) {
        log.info("------------------/fileInfo/examineList-------------begin---------");
        Integer pageNo = 1;
        Integer pageNum = 10;
        Result result = new Result();
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }

        //普通用户登陆传
        if (map.get("userId") == null || (map.get("userId").equals(""))) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("查看列表失败");
            return result;
        }
        FileInfo fileInfo = new FileInfo();

        fileInfo.setFileUserId(Integer.parseInt(map.get("userId").toString()));

        if (map.get("fileKeyWord") != null && !(map.get("fileKeyWord").equals(""))) {
            fileInfo.setFileKeyWord(map.get("fileKeyWord").toString());
        }
        if (map.get("fileDescribe") != null && !(map.get("fileDescribe").equals(""))) {
            fileInfo.setFileDescribe(map.get("fileDescribe").toString());
        }
        if (map.get("fileName") != null && !(map.get("fileName").equals(""))) {
            fileInfo.setFileName(map.get("fileName").toString());
        }
        PageHelper.startPage(pageNo, pageNum);
        List<FileInfo> list = fileInfoService.selectListByUserId(fileInfo);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setUrl("http://" + fileServerIp + list.get(i).getFilePath() + list.get(i).getFileName());
                if (StringUtils.isNotBlank(list.get(i).getKnowledgeIds())) {
                    String[] strings = list.get(i).getKnowledgeIds().split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < strings.length; j++) {
                        Knowledge knowledge = knowledgeService.selectById(Integer.parseInt(strings[j].trim()));
                        buffer.append(knowledge.getKnowledgeName() + ",");
                    }
                    list.get(i).setKnowledgeIds(buffer.toString().substring(0, buffer.toString().length() - 1));
                }
                if (list.get(i).getPdfUrl() != null && !(list.get(i).getPdfUrl().equals(""))) {
                    list.get(i).setPdfUrl(send + list.get(i).getPdfUrl());
                }
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("查询成功");
        result.setData(pageInfo);
        log.info("------------------/fileInfo/examineList-------------end---------");
        return result;
    }


    /**
     * 今天上传量  以及下载量
     *
     * @return
     */
    @PostMapping("/todayUploadOrDownLoadFileNum")
    @ResponseBody
    public Result todayUploadOrDownLoadFileNum() {
        log.info("------------------/fileInfo/todayUploadOrDownLoadFileNum-------------begin---------");
        Result result = new Result();
        Map<String, Integer> map = new HashMap<>();
        Integer uploadNum = 0;
        Integer downloadNum = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String begin = df.format(new Date()) + " 00:00:00";
        String end = df.format(new Date()) + " 23:59:59";
        try {
            uploadNum = fileInfoLogService.selectUploaNumByBeginTimeAndEndTime(begin, end);//上传数量
        } catch (Exception e) {
            log.error("-------------获取今天上传量失败----------------------------");
        }
        try {
            downloadNum = fileInfoLogService.selectDownloadNumByBeginTimeAndEndTime(begin, end);//上传数量
        } catch (Exception e) {
            log.error("-------------获取今天下载量失败----------------------------");
        }

        map.put("uploadNum", uploadNum);
        map.put("downloadNum", downloadNum);
        result.setCode(ResultCode.SUCCESS);
        result.setData(map);
        log.info("------------------/fileInfo/todayUploadOrDownLoadFileNum-------------end---------");
        return result;
    }


    /**
     * 最新上传 和最热下载
     *
     * @return
     */
    @PostMapping("/hotNewUploadAndHotDownload")
    @ResponseBody
    public Result hotNewUpload() {
        log.info("------------------/fileInfo/hotNewUploadAndHotDownload-------------begin---------");
        Result result = new Result();
        Map<Object, Object> map = new HashMap<>();
        List<Map<Object, Object>> hotNewUploadMap = new ArrayList<>();
        try {
            hotNewUploadMap = fileInfoService.selectHotNewUpload();//最新上传
            for (int i = 0; i < hotNewUploadMap.size(); i++) {
                hotNewUploadMap.get(i).put("url", "http://" + fileServerIp + hotNewUploadMap.get(i).get("filePath").toString() + hotNewUploadMap.get(i).get("fileName").toString());
                if (hotNewUploadMap.get(i).get("pdfUrl") != null && !(hotNewUploadMap.get(i).get("pdfUrl").equals(""))) {
                    hotNewUploadMap.get(i).put("pdfUrl", send + hotNewUploadMap.get(i).get("pdfUrl").toString());
                } else {
                    hotNewUploadMap.get(i).put("pdfUrl", "");
                }
            }
        } catch (Exception e) {
            log.error("-------------获取最新上传失败----------------------------");
        }


        List<Map<Object, Object>> hotDownloadMap = new ArrayList<>();
        try {
            hotDownloadMap = fileInfoService.selectHotDownload();//最热下载
            for (int i = 0; i < hotDownloadMap.size(); i++) {
                hotDownloadMap.get(i).put("url", "http://" + fileServerIp + hotDownloadMap.get(i).get("filePath").toString() + hotDownloadMap.get(i).get("fileName").toString());
                if (hotDownloadMap.get(i).get("pdfUrl") != null && !(hotDownloadMap.get(i).get("pdfUrl").equals(""))) {
                    hotDownloadMap.get(i).put("pdfUrl", send + hotDownloadMap.get(i).get("pdfUrl").toString());
                    hotDownloadMap.get(i).put("excelDestrible", "文件内容");
                } else {
                    hotDownloadMap.get(i).put("pdfUrl", "");
                    hotDownloadMap.get(i).put("excelDestrible", "文件内容");
                }
            }
        } catch (Exception e) {
            log.error("-------------获取最热下载失败----------------------------");
        }
        map.put("hotNewUploadMap", hotNewUploadMap);
        map.put("hotDownloadMap", hotDownloadMap);
        result.setCode(ResultCode.SUCCESS);
        result.setData(map);
        log.info("------------------/fileInfo/hotNewUploadAndHotDownload-------------end---------");
        return result;
    }


    /**
     * 预览
     *
     * @return
     */
//    @CrossOrigin(origins = "*", maxAge = 300)
    @PostMapping("/preview")
    @ResponseBody
    public Result preview(@RequestBody Map<String, Object> map) throws IOException {
        log.info("------------------/fileInfo/preview-------------begin---------");
        Result result = new Result();
        Map<Object, Object> hashMap = new HashMap<>();
        String pdfName = "";
        if (map.get("filePath") == null || map.get("filePath").equals("")) {
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("预览失败");
            return result;
        }
        FileInfo fileInfo = fileInfoService.selectByFilePath(map.get("filePath").toString());
        if (fileInfo == null || fileInfo.equals("")) {
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("预览失败");
            return result;
        }
        if (fileInfo.getFileExt().equals("zip") || fileInfo.getFileExt().equals("rar") || fileInfo.getFileExt().equals("tar")||fileInfo.getFileExt().equals("jar")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("预览失败-----压缩包目前不支持预览");
            return result;
        }
        //当前文件已经是一个pdf文件了  不需要再次生成
        if (fileInfo.getFileExt().equals("pdf")) {
            hashMap.put("url", "http://" + fileServerIp + fileInfo.getFilePath() + fileInfo.getFileName());
            result.setCode(ResultCode.SUCCESS);
            result.setData(hashMap);
            return result;
        }
        if (fileInfo.getPdfUrl() != null && !(fileInfo.getPdfUrl().equals(""))) {
            File file = new File(send + fileInfo.getPdfUrl());
            if (file.isFile() && !(file.isDirectory())) {
                hashMap.put("url", "http://" + fileServerIp + fileInfo.getPdfUrl());
            }
        } else {
            File file = new File(send + fileInfo.getFilePath() + fileInfo.getFileName());
            File newFile = new File(send + fileRoot + pdf);//转换之后文件生成的地址
//            if (!newFile.exists()) {
//                newFile.mkdirs();
//            }
            if (!newFile.getParentFile().exists()) {
                newFile.setWritable(true, false);//写操作需要添加
                newFile.getParentFile().mkdirs();
                newFile.createNewFile();
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
            String time = df.format(new Date());
            StringBuffer buffer = new StringBuffer();
            try {
                //            pdfName = fileInfo.getFileName().substring(0, fileInfo.getFileName().lastIndexOf(".")) + ".pdf";
                buffer.append(fileInfo.getFileName().substring(0, fileInfo.getFileName().lastIndexOf(".")));
                buffer.append("-");
                buffer.append(time);
                buffer.append(".pdf");
                pdfName = buffer.toString();
//                OnlinePreview onlinePreview = new OnlinePreview();
//                OfficeManager officeManager = onlinePreview.getOfficeManager(getOfficeHome(), host);
//                OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//                File file1 = onlinePreview.converterFile(file, (send + fileRoot + pdf + pdfName), converter);
                converter.convert(file).to(new File(send + fileRoot + pdf + pdfName)).execute();

            } catch (Exception e) {
                log.error("---------/fileInfo/preview-----文件转换失败--------");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("预览失败");
                return result;
            }
            try {
                fileInfo.setPdfUrl(fileRoot + pdf + pdfName);
                fileInfoService.updateFileInfo(fileInfo);
            } catch (Exception e) {
                log.error("---------/fileInfo/preview-----数据库保存失败--------");
            }
            //需要转换的文件
            hashMap.put("url", "http://" + fileServerIp + fileRoot + pdf + pdfName);
        }
        result.setCode(ResultCode.SUCCESS);
        result.setData(hashMap);
        log.info("------------------/fileInfo/preview-------------end---------");
        return result;
    }

    //使用字节流   字符流下载txt文件
    @PostMapping("/ioDownload")
    @ResponseBody
    public void ioDownload(HttpServletResponse response, @Param("filePath") String filePath, @Param("fileName") String fileName) throws Exception {
        log.debug(filePath);
        response.setContentType("multipart/form-data");
        filePath = send + filePath + fileName;
        // 服务器保存的文件地址，即你要下载的文件地址（全路径）
        File file = new File(filePath);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
