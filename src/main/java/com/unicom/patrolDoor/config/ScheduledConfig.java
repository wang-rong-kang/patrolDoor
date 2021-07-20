package com.unicom.patrolDoor.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.usercenter.sso.util.HttpUtil;
import com.chinaunicom.usercenter.sso.util.app.HttpUtilApp;
import com.unicom.patrolDoor.dao.UserLogMapper;
import com.unicom.patrolDoor.dao.UserMapper;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.entity.UserLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/4/16 15:21
 */
@Component
public class ScheduledConfig {
    private static final Logger log = LogManager.getLogger(ScheduledConfig.class);
    private static String str1 = "";
    private static String str2 = "";
    @Resource
    private UserLogMapper userLogMapper;

    @Resource
    private UserMapper userMapper;

    @Value(value = "${smart.portal.privatekey}")
    private String privateKey;

    @Value(value = "${smart.portal.appID}")
    private String appID;

    @Value(value = "${smart.portal.serviceKeyCheckLogin}")
    private String appCheckLogin;

    @Value(value = "${smart.portal.ssoConfig}")
    private String ssoConfig;

    @Value(value = "${smart.portal.appCheckLoginAdress}")
    private String appCheckLoginAdress;

    /**
     * 查看是否有一直在线的用户  为了统计今日在线人数数量
     */
    @Scheduled(cron = "00 01 00 * * ?")//每天0点01分执行
    public void checkOnline() throws Exception {
        /**
         * 获取昨天的日期
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String yesterdayTime = sdf.format(calendar.getTime());
        List<UserLog> userLogList = userLogMapper.selectByBeginTimeAndIsOnLineAndEndTime(yesterdayTime, 1);
        /**
         * 获取今天的日期
         */
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String begin = df.format(new Date());
        if (userLogList.size() > 0) {
            for (UserLog userLog : userLogList) {
//                userLog.setIsNo(0);
                userLog.setIsNo(1);
                userLog.setCreateTime(begin);
                userLog.setOutTime(begin);
                userLogMapper.updateByPrimaryKeySelective(userLog);//将原来的进行修改
            }
        }

        //验证token是否过期  如果过期：修改登陆状态
        List<User> userList = userMapper.selectByStatuAndIsLocal();
        HttpUtilApp getHttpUti2 = HttpUtilApp.getIstance(appID, ssoConfig, ssoConfig);
        int i = 1;
        if (userList.size() > 0) {
            for (User user : userList) {
                //验证智慧门户的token过期

                System.out.println("\n<<<------------------------start APP_CHECK_LOGIN----------------------------------->>>\n");
                //各平台参数数据SvcCont数据
                JSONObject token = new JSONObject();
                token.put("token", user.getToken());
                JSONObject parameters = new JSONObject();
                parameters.put("parameters", token);
                //1.各平台报文头数据TcpCont数据
                JSONObject TcpCont2 = new JSONObject();
                TcpCont2.put("appID", appID);
                getTime();
                TcpCont2.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
                TcpCont2.put("reqTime", str1);//请求时间格式
                TcpCont2.put("serviceKey", appCheckLogin);//服务编码
                //2.组装报文传输
                JSONObject param2 = new JSONObject();
                param2.put("tcpCont", TcpCont2);//报文头
                param2.put("svcCont", parameters);//报文体(加密后)

                System.out.println("<<<-APP_CHECK_LOGIN发送内容ReqParam->>> " + param2);
                String agetUrl3 = HttpUtilApp.getUserCenterAddr(HttpUtilApp.APP_CHECK_LOGIN, appCheckLoginAdress);
                System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl3);
                String rs3 = HttpUtil.postJson(agetUrl3, param2.toString());

                //输出响应
                System.out.println("===APP_CHECK_LOGIN=：" + rs3);
                Map<String, Map<String,String>> smartMap = JSON.parseObject(rs3, Map.class);
//                System.out.println(smartMap.get("tcpCont"));
                Map<String,String> stringStringMap=smartMap.get("tcpCont");
                if(stringStringMap.get("rspMsg").equals("0")&&stringStringMap.get("rspMsg").equals("操作成功")){
                    //只要code值不是0并且不是操作成功
                }else{
                    //修改数据库状态  将登陆状态修改成未登录
                    user.setOnLine(0);
                    userMapper.updateByPrimaryKeySelective(user);
                }
            }
        }
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
}
