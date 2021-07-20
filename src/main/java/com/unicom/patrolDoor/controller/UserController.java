package com.unicom.patrolDoor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.usercenter.sso.util.HttpUtil;
import com.chinaunicom.usercenter.sso.util.app.Base64;
import com.chinaunicom.usercenter.sso.util.app.HttpUtilApp;
import com.chinaunicom.usercenter.sso.util.app.RSAApi;
import com.chinaunicom.usercenter.sso.util.app.RSASignature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.entity.UserLog;
import com.unicom.patrolDoor.service.UserLogService;
import com.unicom.patrolDoor.service.UserService;
import com.unicom.patrolDoor.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author wrk
 * @Date 2021/3/31 14:03
 */
//@CrossOrigin(origins = "*", maxAge = 300)
@RestController
@RequestMapping("/user")
public class UserController {

    private static String str1 = "";
    private static String str2 = "";

    private static final Logger log = LogManager.getLogger(UserController.class);
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

    @Value(value = "${file.file}")
    private String knowledgeDeleteFile;

    @Value(value = "${file.serverIp}")
    private String fileServerIp;

    @Value(value = "${file.sensitiveTxt}")
    private String sensitiveTxt;

    @Value(value = "${email.host}")
    private String host;

    @Value(value = "${email.userNameAndFrom}")
    private String userNameAndFrom;

    @Value(value = "${email.authorizationCode}")
    private String authorizationCode;

    @Value(value = "${cloud.email}")
    private String cloudEmail;

    @Value(value = "${smart.portal.privatekey}")
    private String privateKey;

    @Value(value = "${smart.portal.appID}")
    private String appID;

    @Value(value = "${smart.portal.serviceKey}")
    private String appLogin;

    @Value(value = "${smart.portal.serviceKeyCheckLogin}")
    private String appCheck;

    @Value(value = "${smart.portal.ssoConfig}")
    private String ssoConfig;

    @Value(value = "${smart.portal.appLoginAdress}")
    private String appLoginAdress;

    @Value(value = "${smart.portal.appCheckLoginAdress}")
    private String appCheckAdress;

    @Resource
    private UserService userService;

    @Resource
    private UserLogService userLogService;

    /**
     * @api {Post} /user/login 01-登陆
     * @apiVersion 1.0.0
     * @apiGroup 门户
     * @apiName 01-登陆
     * @apiDescription 登陆
     * @apiSuccess (入参) {username} String 账号
     * @apiSuccess (入参) {password} String 密码
     * @apiSuccessExample 入参示例
     * {
     * "username":"123456",
     * "password":"123456"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"登陆成功"
     * "data":{
     * "id": 1,
     * "name": "abc",
     * "gmtCreate": null,
     * "gmtModified": null,
     * "onLine": 1,
     * "isNo": 1,
     * "number": null,
     * "password": null}
     * }
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestBody Map<Object, Object> map) throws Exception {
        log.info("------------------/user/login-------------begin---------");
        Result result = new Result();
        User user = new User();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String begin = df.format(new Date());
        if (map.get("unicomuser") != null && !(map.get("unicomuser").equals("")) && map.get("unicomuser").toString().equals("1")) {
            //智慧门户登陆
            // < modified by Billy Lu in 2021.07.09
            //先执行RSAKey生成公钥私钥文件。
            JSONObject parameters0 = new JSONObject();
            parameters0.put("appID", "HQ202106151645581404721836284489730");
            parameters0.put("passWord", map.get("password"));
            parameters0.put("provinceCode","");
            parameters0.put("staffID", map.get("username"));
            JSONObject param0 = new JSONObject();
            param0.put("parameters", parameters0);
            //System.out.println("<<<-测试字符串1->>> " + param0.toJSONString());
            String SvcCont = param0.toJSONString();

            //1.各平台获取私钥
            System.out.println("<<<-秘钥privateKey->>> " + privateKey);
            //各平台参数数据SvcCont数据
            //String SvcCont = "{\"parameters\":{\"appID\":\"HQ202106151645581404721836284489730\",\"passWord\":\"\",\"provinceCode\":\"\",\"staffID\":\"lub16\"}}";
            System.out.println("<<<-业务参数svcCont->>> " + SvcCont);
            //2.SvcCont数据签名
            String sign = RSASignature.sign(SvcCont, privateKey);
            System.out.println("<<<-进行业务内容签名svcContSign->>> " + sign);
            //3.SvcCont数据加密
            String encrypt = Base64.encode(RSAApi.encrypt(RSAApi.loadPrivateKeyByStr(privateKey), SvcCont.getBytes()));
            System.out.println("<<<-进行业务内容rsa加密svcContEncrypt->>> " + encrypt);
            //各平台报文头数据TcpCont数据
            JSONObject TcpCont = new JSONObject();
            TcpCont.put("appID", appID);//appid编码
            getTime();
            TcpCont.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
            TcpCont.put("reqTime", str1);//请求时间格式
            TcpCont.put("serviceKey", appLogin);//服务编码
            TcpCont.put("signature", sign);//签名
            //4.组装报文传输
            JSONObject param = new JSONObject();
            param.put("tcpCont", TcpCont);//报文头
            param.put("svcCont", encrypt);//报文体(加密后)
            System.out.println("<<<-APP_LOGIN发送内容ReqParam->>> " + param);
            System.out.println("<<<------------------------加密发送完毕----------------------------------->>>");
            System.out.println("<<<------------------------开始进行rsa加密传输----------------------------------->>>");
            //--微服务接收报文后------------------------------------------------------------------------------//

            int i = 0;

            //APP_LOGIN
            System.out.println("\n<<<------------------------start APP_LOGIN----------------------------------->>>\n");
            HttpUtilApp getHttpUti2 = HttpUtilApp.getIstance(appID, ssoConfig, ssoConfig);
            String agetUrl222 = getHttpUti2.getUserCenterAddr(HttpUtilApp.APP_LOGIN, appLoginAdress);
            System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl222);
            String rs = HttpUtil.postJson(agetUrl222, param.toString());
            System.out.println("===APP_LOGIN=：" + rs);
            JSONObject getJson = JSON.parseObject(rs);
            JSONObject getSvcRetCont = (JSONObject)getJson.get("svcRetCont");
            JSONObject getResult = (JSONObject)getSvcRetCont.get("result");
            String getToken = getResult.getString("token");
            System.out.println("===getToken=：" + getToken);
            //String getProvince = getResult.getString("staffName");
            System.out.println("===getProvince=：" + getResult.toString());
            if(StringUtils.isNotBlank(getToken)){
                // < modified by Billy Lu in 2021.07.09
                //APP_CHECK_LOGIN
                System.out.println("\n<<<------------------------start APP_CHECK_LOGIN----------------------------------->>>\n");
                //各平台参数数据SvcCont数据
                JSONObject token = new JSONObject();
                token.put("token", getToken);
                JSONObject parameters = new JSONObject();
                parameters.put("parameters", token);
                //1.各平台报文头数据TcpCont数据
                JSONObject TcpCont2 = new JSONObject();
                TcpCont2.put("appID", appID);
                getTime();
                TcpCont2.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
                TcpCont2.put("reqTime", str1);//请求时间格式
                TcpCont2.put("serviceKey", appCheck);//服务编码
                //2.组装报文传输
                JSONObject param2 = new JSONObject();
                param2.put("tcpCont", TcpCont2);//报文头
                param2.put("svcCont", parameters);//报文体(加密后)
                System.out.println("<<<-APP_CHECK_LOGIN发送内容ReqParam->>> " + param2);
                String agetUrl3 = getHttpUti2.getUserCenterAddr(HttpUtilApp.APP_CHECK_LOGIN, appCheckAdress);
                System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl3);
                String rs3 = HttpUtil.postJson(agetUrl3, param2.toString());
                JSONObject getJson3 = JSON.parseObject(rs3);
                JSONObject getSvcRetCont3 = (JSONObject)getJson3.get("svcRetCont");
                JSONObject getResult3 = (JSONObject)getSvcRetCont3.get("result");
                String UserName = getResult3.getString("staffName"); // 中文名字
                String UserMail = getResult3.getString("mail"); // 用户系统邮箱
                String IsOnline = getResult3.getString("staffStatus"); // 是否在线，“1”为在线
                String UserID = getResult3.getString("staffID"); // 用户ID
                System.out.println("UserName：" + UserName + ",UserMail=:" + UserMail + ",UserID=:" + UserID);
                //   modified by Billy Lu in 2021.07.09 >

                //Map<String, String> smartMap = JSON.parseObject(JSON.toJSONString(map.get("parameters")), Map.class);
                List<User> list = userService.selectByNumber(UserID);
                if (list.size() > 0) {
                    list.get(0).setToken(getToken);
//                    userService.updateUser(list.get(0));
                    user = list.get(0);
                    user.setEmail(UserMail);
                    userService.updateUser(user);
                } else {
                    user.setToken(getToken);
                    user.setNumber(UserID);
                    user.setIsLocal(0);//智慧门户
                    user.setPassword("********************************");
                    user.setProvinceCode(""); // 暂时空缺，待调研 BillyLu 07.09
                    user.setName(UserName); // 设置为中文名字
                    user.setEmail(UserMail);
                    user.setGmtCreate(begin);
                    user.setGmtModified(begin);
                    Integer num=userService.selectMaxId();
                    user.setId(num+1);
                    userService.insertUser(user);
                }
                userService.updateIsOnLineByUserId(user.getId());
                result.setCode(ResultCode.SUCCESS);
                result.setMessage("智慧门户登陆成功");
                user.setOnLine(1);
                user.setNumber(map.get("username").toString());
            }
            else {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("您输入的智慧门户账号或密码有误，请重新输入");
                return result;
            }
        } else {
            if (map.get("username") == null || map.get("username").equals("")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("您输入的账号为空，请重新输入");
                return result;
            }
            if (map.get("password") == null || map.get("password").equals("")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("您输入的密码为空，请重新输入");
                return result;
            }
            user = userService.selectUserByNumberAndPassword(map.get("username").toString(), map.get("password").toString());
            if (user == null) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("您输入的账号或密码有误，请重新输入");
                return result;
            }
            if (user.getStatu() == 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("账号不存在，请重新输入");
                return result;
            }
            userService.updateIsOnLineByUserId(user.getId());
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("登陆成功");
            user.setOnLine(1);
            user.setNumber(map.get("username").toString());
//            user.setPassword(map.get("password").toString());
        }
        //修改日志表
        try {
            UserLog userLog = new UserLog();

            userLog.setCreateTime(begin);
            userLog.setOutTime(begin);
            userLog.setUserId(user.getId());
            userLog.setIsNo(1);
            userLogService.insertUserLog(userLog);
            Integer num = userLogService.selectMaxId();
            user.setLog(num);
        } catch (Exception e) {
            log.error("-----插入日志表(user_log)失败---登陆人ID:" + user.getId());

        }
        user.setLoginTime(begin);
        result.setData(user);
        log.info("------------------/user/login-------------end---------");
        return result;
    }

    /**
     * @api {Post} /user/register 02-注册
     * @apiVersion 1.0.0
     * @apiGroup 门户
     * @apiName 02-注册
     * @apiDescription 注册
     * @apiSuccess (入参) {name}  昵称 String
     * @apiSuccess (入参) {number} String 账号（账号必须是6到8位由字母跟数字组成）
     * @apiSuccess (入参) {password} String 密码
     * @apiSuccess (入参) {email} String 邮箱
     * @apiSuccess (入参) {verificationCode} String 验证码
     * @apiSuccessExample 入参示例
     * {
     * "name":"测试",
     * "number":"12345678",
     * "password":"12345678",
     * "email":"ceshi@qq.com",
     * "verificationCode":"123456"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"注册成功"
     * "data":{}
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result register(@RequestBody Map<Object, Object> map) throws Exception {
        log.info("------------------/user/register-------------begin---------");
        Result result = new Result();
        User user = new User();
        EmailUtil emailUtil = new EmailUtil();
        TimeReduceUtil timeReduceUtil = new TimeReduceUtil();
        if ((map.get("name") == null || map.get("name").equals(""))) {
            result.setMessage("失败：昵称不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("number") == null || map.get("number").equals(""))) {
            result.setMessage("失败：账号不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("password") == null || map.get("password").equals(""))) {
            result.setMessage("失败：密码不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("email") == null || map.get("email").equals(""))) {
            result.setMessage("邮箱不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("verificationCode") == null || map.get("verificationCode").equals(""))) {
            result.setMessage("失败：验证码不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }

        if ((map.get("password").toString().length() != 32)) {
            result.setMessage("失败：密码必须大于8位");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        //生成验证码的时间
        if (map.get("date") == null || map.get("date").equals("")) {
            result.setMessage("失败");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        DateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long reduce = timeReduceUtil.timeReduce(d.format(new Date()), map.get("date").toString());
        //验证码不区分大小写
        if (map.get("randomCode") != null && !(map.get("randomCode").equals("")) && reduce < 5 && reduce != -1) {
            //输入的验证码和生成的不一样  或者是验证码生成时间已经超过五分钟
            if (!(map.get("randomCode").toString().equalsIgnoreCase(map.get("verificationCode").toString()))) {
                result.setMessage("失败：验证码已经失效,请重新获取");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        } else {
            result.setMessage("失败：验证码已经失效,请重新获取");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("number").toString() != null && !(map.get("number").toString().equals("")))) {
            List<User> userList = userService.selectByNumber(map.get("number").toString());
            if (userList.size() > 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("此账号已经存在");
                return result;
            }
//            String num="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,8}$";
            String num = "^[0-9A-Za-z]{6,8}$";
            boolean f = map.get("number").toString().matches(num);
            if (f == false) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("账号必须是6到8位由数字和字母组成");
                return result;
            }

            user.setNumber(map.get("number").toString());
        }
        /**
         * 验证邮箱格式
         */
        String emailResult = emailUtil.emailVerification((map.get("email").toString()), cloudEmail);
        if (!emailResult.equals("1")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage(emailResult);
            return result;
        }
        SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
        String response = sensitivewordFilter.replaceSensitiveWord(map.get("name").toString(), 1, "*", sensitiveTxt);
        if (response.equals("false")) {
            user.setName(map.get("name").toString());
        } else {
            result.setMessage("失败：您输入的昵称包括敏感词语：" + response + ",请您修改后提交");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if (map.get("headFile") != null && !(map.get("headFile").equals(""))) {
            user.setHeadFile(map.get("headFile").toString());
        }

        user.setNumber(map.get("number").toString());
        user.setPassword(map.get("password").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//??????
        user.setEmail(map.get("email").toString());
        user.setGmtCreate(df.format(new Date()));
        user.setGmtModified(df.format(new Date()));
        user.setOnLine(0);
        user.setIsNo(0);
        user.setStatu(1);
        user.setIsLocal(1);
        try {
            userService.insertUser(user);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("注册成功");
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("注册失败");
        }
        return result;
    }

    /**
     * @api {Post} /user/forgetPassword 03-忘记密码
     * @apiVersion 1.0.0
     * @apiGroup 门户
     * @apiName 03-忘记密码
     * @apiDescription 忘记密码
     * @apiSuccess (入参) {number} String 账号（账号必须是6到8位由字母跟数字组成）
     * @apiSuccess (入参) {email} String 邮箱
     * @apiSuccess (入参) {verificationCode} String 验证码
     * @apiSuccessExample 入参示例
     * {
     * "number":"12345678",
     * "email":"ceshi@qq.com",
     * "verificationCode":"123456"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"密码已经发送到您邮箱，请您及时查收"
     * "data":{}
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    @ResponseBody
    public Result forgetPassword(@RequestBody Map<Object, Object> map) throws Exception {
        Map m = new HashMap();
        EmailUtil emailUtil = new EmailUtil();
        TimeReduceUtil timeReduceUtil = new TimeReduceUtil();
        System.out.println("开始" + System.currentTimeMillis());
        log.info("------------------/user/forgetPassword-------------begin---------");
        Result result = new Result();
        if ((map.get("number") == null || map.get("number").equals(""))) {
            result.setMessage("失败：账号不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        if ((map.get("email") == null || map.get("email").equals(""))) {
            result.setMessage("失败：邮箱不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        /**
         * 验证邮箱格式
         */
        String emailResult = emailUtil.emailVerification((map.get("email").toString()), cloudEmail);
        if (!emailResult.equals("1")) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage(emailResult);
            return result;
        }
        if ((map.get("verificationCode") == null || map.get("verificationCode").equals(""))) {
            result.setMessage("失败：验证码不能为空");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        //生成验证码的时间
        if (map.get("date") == null || map.get("date").equals("")) {
            result.setMessage("失败");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }

        DateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long reduce = timeReduceUtil.timeReduce(d.format(new Date()), map.get("date").toString());
        //验证码不区分大小写
        if (map.get("randomCode") != null && !(map.get("randomCode").equals("")) && reduce < 5 && reduce != -1) {
            //输入的验证码和生成的不一样  或者是验证码生成时间已经超过五分钟
            if (!(map.get("randomCode").toString().equalsIgnoreCase(map.get("verificationCode").toString()))) {
                result.setMessage("失败：验证码已经失效,请重新获取");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        } else {
            result.setMessage("失败：验证码已经失效,请重新获取");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        User u = userService.selectUserByNumberAndEmail(map.get("number").toString(), map.get("email").toString());
        if (u == null || u.equals("")) {
            result.setMessage("失败：您输入的账号或邮箱有误,请您修改后提交");
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            return result;
        }
        /**
         * 生成8位的随机密码
         */
        StringBuilder str = new StringBuilder();//定义变长字符串
        Random random = new Random();
        //随机生成数字，并添加到字符串
        for (int i = 0; i < 8; i++) {
            str.append(random.nextInt(10));
        }

        //此时给邮箱发信息
        u.setPassword(MD5Util.getMD5(str.toString()));
        u.setOnLine(0);

        try {
            userService.updateUser(u);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("密码重置成功,新密码已发到您邮箱,请您及时查收");
            m.put("code", str.toString());
            m.put("email", (map.get("email").toString()));
            result.setData(m);
            return result;
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("密码重置失败");
            return result;
        }
    }

    /**
     * @api {Post} /user/sendMessage 01-给邮件发密码
     * @apiVersion 1.0.0
     * @apiGroup 其它
     * @apiName 01-给邮件发密码
     * @apiDescription 给邮件发密码
     * @apiSuccess (入参) {email} String 邮箱
     * @apiSuccess (入参) {code} String 密码
     * @apiSuccessExample 入参示例
     * {
     * "email":"ceshi@qq.com",
     * "code":"12345678"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"发送成功"
     * "data":{}
     */
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    @ResponseBody
    public Result send(@RequestBody Map<Object, Object> map) {
        Result result = new Result();
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);// 链接服务器
        javaMailSender.setUsername(userNameAndFrom);// 邮箱账号
        javaMailSender.setPassword(authorizationCode);// 授权码
        javaMailSender.setDefaultEncoding("UTF-8");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            helper.setFrom(userNameAndFrom, "通知");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            helper.setTo(new String[]{map.get("email").toString()});
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            helper.setSubject("密码更改");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            helper.setText("新密码为:" + map.get("code").toString(), false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(message);
        result.setCode(ResultCode.SUCCESS);
        return result;
    }


    /**
     * @api {Post} /user/out 05-退出
     * @apiVersion 1.0.0
     * @apiGroup 门户
     * @apiName 05-退出
     * @apiDescription 退出
     * @apiSuccess (入参) {id} Integer 登陆用户Id
     * @apiSuccessExample 入参示例
     * {
     * "id":"1"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"退出成功"
     * "data":{}
     */
    @RequestMapping(value = "/out", method = RequestMethod.POST)
    @ResponseBody
    public Result out(@RequestBody Map<Object, Object> map) {
        log.info("------------------/user/out-------------begin---------");

        Result result = new Result();
        if (map.get("id") != null && !(map.get("id").equals(""))) {
            userService.updateIsOnLineById(Integer.parseInt(map.get("id").toString()));
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("退出成功");
        } else {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("退出失败-----参数不能为空");
            return result;
        }
        UserLog userLog = new UserLog();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String begin = df.format(new Date());
        userLog.setOutTime(begin);
        userLog.setUserId(Integer.parseInt(map.get("id").toString()));
        userLog.setIsNo(0);
        if (map.get("log") != null && !(map.get("log").equals(""))) {
            try {
                userLog.setId(Integer.parseInt(map.get("log").toString()));
                userLogService.updateUserLog(userLog);
            } catch (Exception e) {
                log.error("-----插入日志表(user_log)失败---登陆人ID:" + Integer.parseInt(map.get("id").toString()));
            }
        } else {
            try {
                userLogService.insertUserLog(userLog);
            } catch (Exception e) {
                log.error("-----插入日志表(user_log)失败---登陆人ID:" + Integer.parseInt(map.get("id").toString()));
            }
        }

        log.info("------------------/user/out-------------end---------");

        return result;
    }

    /**
     * @api {Post} /user/add 01-添加用户
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 01-添加用户
     * @apiDescription 添加用户
     * @apiSuccess (入参) {name} String 昵称
     * @apiSuccess (入参) {isNo} Integer 是否是管理员（0: 不是  1:  是）
     * @apiSuccess (入参) {number} String 账号
     * @apiSuccess (入参) {password} String 密码
     * @apiSuccess (入参) {email} String 邮箱
     * @apiSuccess (入参) {statu} Integer 用户状态  0:已删除  1:存在
     * @apiSuccess (入参) {headFile} String 头像
     * @apiSuccessExample 入参示例
     * {
     * "name":"test",
     * "isNo":1,
     * "number":"12345678",
     * "password":"22234567",
     * "email":"ceshi@qq.com",
     * "statu": 1,
     * "headFile":"ceshi.jpg"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"添加成功"
     * "data":{}
     */
    @PostMapping("/add")
    public Result add(@Param("id") Integer id,
                      @Param("name") String name,
                      @Param("isNo") Integer isNo,
                      @Param("number") String number,
                      @Param("password") String password,
                      @Param("passwordNew") String passwordNew,
                      @Param("statu") Integer statu,
                      @Param("headFile") String headFile,
                      @Param("email") String email,
                      @Param("tagIdLunTan") String tagIdLunTan,
                      @Param("tagIdZhiShiKu") String tagIdZhiShiKu) throws Exception {
        log.info("------------------/user/add-------------begin---------");
        EmailUtil emailUtil = new EmailUtil();
        Result result = new Result();
        User user = new User();
        user.setHeadFile(headFile);
        user.setId(id);
        if (isNo != null && !(isNo.equals(""))) {
            user.setIsNo(isNo);
            if (isNo == 1) {
                user.setTagIds(" ");
            }
            if (isNo == 2) {
                user.setTagIds(tagIdLunTan);
            }
            if (isNo == 3) {
                user.setTagIds(tagIdZhiShiKu);
            }
        }

        if (name != null && !(name.equals(""))) {
            SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
            String response = sensitivewordFilter.replaceSensitiveWord(name, 1, "*", sensitiveTxt);
            if (response.equals("false")) {
                user.setName(name);
            } else {
                result.setMessage("失败：您输入的昵称信息包括敏感词语：" + response + ",请您修改后提交");
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                return result;
            }
        }

        user.setStatu(statu);
        if (number != null && !(number.equals(""))) {
            List<User> userList = userService.selectByNumber(number);
            if (userList.size() > 0) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("此账号已经存在");
                return result;
            }
//            String num="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,8}$";
            String num = "^[0-9A-Za-z]{6,8}$";
            boolean f = number.matches(num);
            if (f == false) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("账号必须是6到8位由数字和字母组成");
                return result;
            }

            user.setNumber(number);
        }


        user.setPassword(password);
        //调用的修改方法
        if (user.getId() != null && !(user.getId().equals(""))) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            user.setGmtModified(df.format(new Date()));
            if (passwordNew != null && !(passwordNew.equals(""))) {
                if (passwordNew.length() != 32) {//前段进行32位MD5加密
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage("密码错误,密码必须是大于8位");
                    return result;
                } else {
                    user.setPassword(passwordNew);
                }

            } else {
                user.setPassword("");
            }

            if (email != null && !(email.equals(""))) {
                /**
                 * 验证邮箱格式
                 */
                String emailResult = emailUtil.emailVerification(email, cloudEmail);
                if (!emailResult.equals("1")) {
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                    result.setMessage(emailResult);
                    return result;
                } else {
                    user.setEmail(email);
                }
            }
            try {
                userService.updateUser(user);
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("修改失败");
                return result;
            }
            result = new Result();
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("修改成功");
        } else {
            //调用的添加方法
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            user.setGmtCreate(df.format(new Date()));
            user.setGmtModified(df.format(new Date()));
            if (user.getPassword() == null || user.getPassword().equals("")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("密码错误,密码不能是空");
                return result;
            }
            if (user.getPassword().length() != 32) {//前段进行32位MD5加密
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("密码错误,密码必须是大于8位");
                return result;
            }
            if (email == null || email.equals("")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("邮箱错误,邮箱不能是空");
                return result;
            }
            /**
             * 验证邮箱格式
             */
            String emailResult = emailUtil.emailVerification(email, cloudEmail);
            if (!emailResult.equals("1")) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage(emailResult);
                return result;
            } else {
                user.setEmail(email);
            }

            try {
                userService.insertUser(user);
            } catch (Exception e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                result.setMessage("添加失败");
                return result;
            }
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("添加成功");

        }
        log.info("------------------/user/add-------------end---------");

        return result;
    }

    /**
     * @api {Post} /user/add 02-修改用户
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 02-修改用户
     * @apiDescription 修改用户
     * @apiSuccess (入参) {Integer} id 用户Id
     * @apiSuccess (入参) {name} String 昵称
     * @apiSuccess (入参) {isNo} Integer 是否是管理员（0: 不是  1:  是）
     * @apiSuccess (入参) {number} String 账号
     * @apiSuccess (入参) {password} String 密码
     * @apiSuccess (入参) {passwordNew} String 新密码
     * @apiSuccess (入参) {email} String 邮箱
     * @apiSuccess (入参) {statu} Integer 用户状态  0:已删除  1:存在
     * @apiSuccess (入参) {headFile} String 头像
     * @apiSuccessExample 入参示例
     * {
     * "id":"1",
     * "name":"test",
     * "isNo":1,
     * "number":"w223456",
     * "password":"********",
     * "passwordNew":"12345678"
     * "email":"ceshi@qq.com",
     * "statu":1,
     * "headFile":"ceshi.jpg"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"修改成功"
     * "data":{}
     */


    /**
     * @api {Post} /user/delete 03-删除用户
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 03-删除用户
     * @apiDescription 删除用户
     * @apiSuccess (入参) {Integer} id 用户Id
     * @apiSuccessExample 入参示例
     * {
     * "id":"4"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"删除成功"
     * "data":{}
     */
    @PostMapping("/delete")
    public Result delete(@Param("id") Integer id) {
        log.info("------------------/user/delete-------------begin---------");

        Result result = new Result();
        User user = new User();
        user.setId(id);
        user.setStatu(0);
        user.setOnLine(0);//删除了  用户强制下线
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        user.setGmtModified(df.format(new Date()));
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMessage("删除失败");
            return result;
        }
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("删除成功");
        log.info("------------------/user/delete-------------end---------");

        return result;

    }


    /**
     * @api {Post} /user/list 04-未删除(删除)用户列表
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 04-未删除（删除）用户列表
     * @apiDescription 未删除（删除）用户列表
     * @apiSuccess (入参) {Integer} pageNo 当前第几页
     * @apiSuccess (入参) {Integer} pageNum 每页多少条
     * @apiSuccess (入参) {Integer} statu 用户状态 1:未删除用户  0:已删除用户
     * @apiSuccess (入参) {String} name 昵称
     * @apiSuccess (入参) {String} number 账号
     * @apiSuccessExample 入参示例
     * {
     * "pageNo":1,
     * "pageNum":10,
     * "statu":1,
     * "name":"测试",
     * "number":"123456"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "data": {
     * "total": 4,
     * "list":[
     * {
     * "id": 2,
     * "name": "测试",
     * "gmtCreate": "2021-04-01 09:40:23",
     * "gmtModified": "2021-04-05 09:40:23",
     * "onLine": 1,
     * "isNo": null,
     * "number": "123456",
     * "statu": 0
     * }
     * }
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Result onLineList(@RequestBody Map<Object, Object> map) {
        log.info("------------------/user/list-------------begin---------");
        Result result = new Result();
        Integer pageNo = 1;
        Integer pageNum = 10;
        Integer statu = 0;
        String name = "";
        String number = "";
        if (map.get("pageNo") != null && !(map.get("pageNo").equals(""))) {
            pageNo = Integer.parseInt(map.get("pageNo").toString());
        }
        if (map.get("pageNum") != null && !(map.get("pageNum").equals(""))) {
            pageNum = Integer.parseInt(map.get("pageNum").toString());
        }
        if (map.get("statu") != null && !(map.get("statu").equals(""))) {
            statu = Integer.parseInt(map.get("statu").toString());
        }
        if (map.get("name") != null && !(map.get("name").equals(""))) {
            name = map.get("name").toString();
        }
        if (map.get("number") != null && !(map.get("number").equals(""))) {
            number = map.get("number").toString();
        }
        PageHelper.startPage(pageNo, pageNum);
        List<User> userList = userService.selectByStatu(statu, name, number);
        if(userList.size()>0){
            for(User user:userList){
                //如果用户只是单独的注册了  还没有登陆
                if(StringUtils.isBlank(user.getLastLoginTime())){
                    user.setLastLoginTime(user.getGmtCreate());
                }
            }
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        result.setData(pageInfo);
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/user/list-------------end---------");

        return result;

    }

    /**
     * @api {Post} /user/select 05-根据主键id查询用户信息
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 05-根据主键id查询用户信息
     * @apiDescription 根据主键id查询用户信息
     * @apiSuccess (入参) {Integer} id
     * @apiSuccessExample 入参示例
     * {
     * "id":1
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "data": {
     * "id":"1",
     * "name":"test",
     * "isNo":1,
     * "number":"w223456",
     * "password":"********",
     * "email":"ceshi@qq.com",
     * "statu":1,
     * "headFile":"ceshi.jpg"
     * }
     * }
     */
    @PostMapping("/select")
    public Result select(@Param("id") Integer id) {
        log.info("------------------/user/select-------------begin---------");

        Result result = new Result();
        User user = userService.selectUserById(id);
        if (user.getHeadFile() == null || user.getHeadFile().equals("")) {
            user.setHeadFile("");
        }
        //String password = user.getPassword().replaceAll(user.getPassword(), "********");
        String password="";
        if(user.getPassword().equals("********************************")){
            password = user.getPassword().substring(0,7);
        }else{
            password = user.getPassword().replaceAll(user.getPassword(), "********");
        }
        user.setPassword(password);     // userController类中的1084行
        result.setData(user);
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/user/select-------------end---------");
        return result;
    }
    /**
     * 头像上传
     *
     * @param headFile
     * @return
     */
    /**
     * @api {Post} /user/select 06-上传头像
     * @apiVersion 1.0.0
     * @apiGroup 用户
     * @apiName 06-上传头像
     * @apiDescription 上传头像
     * @apiSuccess (入参) {MultipartFile} headFile 头像图片
     * @apiSuccessExample 入参示例
     * {
     * "MultipartFile":"test.jpg"
     * }
     * @apiSuccessExample 响应结果示例
     * {
     * "code":200,
     * "message":"上传成功"
     * "data": {}
     * }
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Result upload(@Param("headFile") MultipartFile headFile) {
        log.info("------------------/user/upload-------------begin---------");

        FileUploadAndDownUtils fileUploadAndDownUtils = new FileUploadAndDownUtils();
        Result result = fileUploadAndDownUtils.upload(headFile, send, fileRoot, userHeadFile, fileServerIp);
        log.info("------------------/user/upload-------------end---------");

        return result;
    }

    /**
     * 统计今天登陆用户的数量
     *
     * @return
     */
    @PostMapping("/todayUserOnLineNum")
    public Result todayUserOnLineNum() {
        log.info("------------------/user/select-------------begin---------");

        Result result = new Result();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String begin = df.format(new Date()) + " 00:00:00";
        String end = df.format(new Date()) + " 23:59:59";
        Integer num = userLogService.selectTodayUserOnLineNum(begin, end);
        result.setData(num);
        result.setMessage("查询成功");
        result.setCode(ResultCode.SUCCESS);
        return result;
    }


    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址.
     * 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 未删除的用户数量
     */
    @PostMapping("/undeleteUserList")
    public Result undeleteUserList() {
        log.info("------------------/user/undeleteUserList-------------begin---------");

        Result result = new Result();
        List<User> userList = userService.selectUserByStatu();
        if (userList.size() > 0) {
            result.setData(userList);
        }
        result.setMessage("查询成功");
        result.setCode(ResultCode.SUCCESS);
        log.info("------------------/user/undeleteUserList-------------end---------");
        return result;
    }


    private static void getTime() {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        Date thisTime = new Date(); // new Date()为获取当前系统时间
        str1 = df1.format(thisTime);
        str2 = df2.format(thisTime);
        int random = (int) (Math.random() * 100000000);
        str2 += random;
    }

    /*
     * @Description:TODO(进行32位的MD5加密)
     *
     * @param String inStr 要解密的字符串
     *
     * @return void
     *
     * 思路：
     * 1.获取到MD5这个对象
     * 2.加密
     */
    public static String md5(String inStr){
        MessageDigest md5 = null;
        // 1.获取MD5这个对象，获取信息摘要对象
        try{
            md5 = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        // 2.将字符串对象中的每一个字符转换成一个字符数组——toCharArray()的用法
        char[] charArray = inStr.toCharArray();
        // 3.定义一个长度和char数组相同的byte字节数组
        byte[] byteArray = new byte[charArray.length];

        // 4.将char数组中的内容放到byte数组中
        for(int i=0;i<charArray.length;i++){
            // {} for{} if{} 里面的代码只有一行的时候 {}可以省略
            byteArray[i] = (byte)charArray[i];
        }

        // 5.md5这个对象 对字节数组进行摘要 得到一个摘要字节数组
        byte[] md5Bytes = md5.digest(byteArray);

        // 6.把摘要字节数组中的每一个字节转换成16进制 并且拼在一起就得到了MD5值
        // StringBuffer：对字符串进行操作 操作字符串的一个工具箱

        StringBuffer hexValue=new StringBuffer();

        for(int i=0;i<md5Bytes.length;i++){
            // 转换成16进制 int 整数型
            int val=((int)md5Bytes[i]) & 0xff;
            // 如果生成的数字未满32位，需要在前面补0

            if(val<16){
                hexValue.append("0");
                // Integer.toHexString(val) 将其转为16进制的方法

                hexValue.append(Integer.toHexString(val));

            }
        }
        return hexValue.toString();
    }



    // 可逆的加密算法 加盐加密
    /*
     * @Description:TODO(可逆的加密算法 加盐加密)
     *
     * @param String inStr 要加密的字符串
     *
     * @return String
     */
    public static String KL(String inStr){
        // 将字符串转换成一个字符数组 给每个字符加密
        char[] charArray = inStr.toCharArray();
        for(int i=0;i<charArray.length;i++){
            /*
             * ^ 异或运算符 如果a[i],'t'两个值不相同 异或结果就为1 如果相同的话就为0
             */
            charArray[i] = (char) (charArray[i] ^ 't');
        }
        String s = new String(charArray);
        return s;
    }

    // MD5加密以后 他会生成不同的字符串 不能反编译
    /*
     *@Description:TODO(反编译)
     *
     *@param String inStr 要解密的字符串
     *
     *@return String
     */
    public static String jm(String inStr){
        // 将字符串转换成为一个字符数组，给每一个字符进行解密
        char[] charArray = inStr.toCharArray();
        for(int i=0;i<charArray.length;i++){
            charArray[i] = (char)(charArray[i] ^ 't');
        }
        String k=new String(charArray);
        return k;
    }
}
