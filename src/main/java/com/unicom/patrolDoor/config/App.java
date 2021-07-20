package com.unicom.patrolDoor.config;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.util.Date;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.usercenter.sso.util.HttpUtil;
import com.chinaunicom.usercenter.sso.util.app.Base64;
import com.chinaunicom.usercenter.sso.util.app.HttpUtilApp;
import com.chinaunicom.usercenter.sso.util.app.RSAApi;
import com.chinaunicom.usercenter.sso.util.app.RSASignature;

public class App {
    private static String str1 = "";
    private static String str2 = "";
    /**
     * @param
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void main(String[] rags) throws Exception {
        //先执行RSAKey生成公钥私钥文件。

        //1.各平台获取私钥
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANbXVoLNFz3Nk1a77CLqR3RZcY4RG12o5yPkXGlwCvR9tSi5RZjO+SU21PFeSf9hc6FelbZm7+UhryO5JmXZ9BJRQ2SNQqFTgF92Xbs3BVOMRqWzcjNRy3xf0l0TYFn7vSq+KCoqJLDMuGjK0fQmdtjSNEwN6Pmvqog8Ob0aiBj7AgMBAAECgYAiZqzFmQ2FqetT0heHp7f1W3UhaH/XSXppduGNSJGYgKCHetM4GrwuR09lrtFugS5AwqJ6aJU1PWWL9NO/L10zXnvl3AQSwaMuozCdFV3V+kRX7uMhkforvFWjVAJgWKnifPOkyFGL2FnSsRSvUl5gP5VM0Ml1ntFof/Nfns/Y4QJBAPW6/zTzgbs55mvKxd4PlhYSeJg3NUrpQhrF8YAks7YdImBs9ySLkYO0yeq9Nfq3kmD0WI0usxzpnTP4LFNilCMCQQDf0d1rMT7HT2UHRk2PVo4Vl5radd7Su9ZJgPA+P5fBi6jEPEE4elT03B/N/scS3ysrfc6RHUKdUN1WhJz4iOlJAkBXwrdOxAD7Swx+RucwDcT+kQ483srLvE79nOBJcdI1ImaapZAGn0oTDchna7gRmOpQcjQGCD2z/8NDQkkiZrydAkA2GmLtoG/OehCv23ywI3ohGg6itE9ynVlFV1e7lF+4t2vKU85oOpoCvpspjmbM1wl2b+jZBzutD3kRiuju4xmRAkEAzNYn4lSNaACrggr9zGQQVYdfmcEig3lVf6nfLEVmkOkFV2uSP1Fi0lbjHpRj//4C7RSlpTLq45M6rKpSj2KfwA==";
        System.out.println("<<<-秘钥privateKey->>> " + privateKey);
        //各平台参数数据SvcCont数据
        String SvcCont = "{\"parameters\":{\"appID\":\"HQ202106151645581404721836284489730\",\"passWord\":\"1qazXSW@\",\"provinceCode\":\"0010\",\"staffID\":\"lub16\"}}";
        System.out.println("<<<-业务参数svcCont->>> " + SvcCont);
        //2.SvcCont数据签名
        String sign = RSASignature.sign(SvcCont, privateKey);
        System.out.println("<<<-进行业务内容签名svcContSign->>> " + sign);
        //3.SvcCont数据加密
        String encrypt = Base64.encode(RSAApi.encrypt(RSAApi.loadPrivateKeyByStr(privateKey), SvcCont.getBytes()));
        System.out.println("<<<-进行业务内容rsa加密svcContEncrypt->>> " + encrypt);
        //各平台报文头数据TcpCont数据
        JSONObject TcpCont = new JSONObject();
        TcpCont.put("appID", "HQ202106151645581404721836284489730");//appid编码
        getTime();
        TcpCont.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
        TcpCont.put("reqTime", str1);//请求时间格式
        TcpCont.put("serviceKey", "app_login");//服务编码
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
        HttpUtilApp getHttpUti2 = HttpUtilApp.getIstance("HQ202106151645581404721836284489730",
                "https://10.124.131.213:8101/uac-sso-config/get_url_config",
                "https://10.124.131.213:8101/uac-sso-config/get_url_config");
        String agetUrl222 = HttpUtilApp.getUserCenterAddr(HttpUtilApp.APP_LOGIN, "https://10.124.131.213:8101/uac-sso-app/app_login");
        System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl222);
        String rs = HttpUtil.postJson(agetUrl222, param.toString());
        System.err.println("==========================APP_LOGIN=：" + rs);
        JSONObject getJson = JSON.parseObject(rs);
        JSONObject getSvcRetCont = (JSONObject)getJson.get("svcRetCont");
        JSONObject getResult = (JSONObject)getSvcRetCont.get("result");
        String getToken = getResult.getString("token");
        System.err.println("==========================getToken=：" + getToken);

        //APP_CHECK_LOGIN
        System.out.println("\n<<<------------------------start APP_CHECK_LOGIN----------------------------------->>>\n");
        //各平台参数数据SvcCont数据
        JSONObject token = new JSONObject();
        token.put("token", getToken);
        JSONObject parameters = new JSONObject();
        parameters.put("parameters", token);
        //1.各平台报文头数据TcpCont数据
        JSONObject TcpCont2 = new JSONObject();
        TcpCont2.put("appID", "HQ202106151645581404721836284489730");
        getTime();
        TcpCont2.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
        TcpCont2.put("reqTime", str1);//请求时间格式
        TcpCont2.put("serviceKey", "app_check_login");//服务编码
        //2.组装报文传输
        JSONObject param2 = new JSONObject();
        param2.put("tcpCont", TcpCont2);//报文头
        param2.put("svcCont", parameters);//报文体(加密后)
        System.out.println("<<<-APP_CHECK_LOGIN发送内容ReqParam->>> " + param2);
        String agetUrl3 = HttpUtilApp.getUserCenterAddr(HttpUtilApp.APP_CHECK_LOGIN, "https://10.124.131.213:8101/uac-sso-app/app_check_login");
        System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl3);
        String rs3 = HttpUtil.postJson(agetUrl3, param2.toString());
        System.out.println("===APP_CHECK_LOGIN=：" + rs3);
        //APP_LOGIN_OUT
        System.out.println("\n<<<------------------------start APP_LOGIN_OUT----------------------------------->>>\n");
        //各平台参数数据SvcCont数据
        JSONObject token4 = new JSONObject();
        token4.put("token", getToken);
        JSONObject parameters4 = new JSONObject();
        parameters4.put("parameters", token4);
        //1.各平台报文头数据TcpCont数据
        JSONObject TcpCont4 = new JSONObject();
        TcpCont4.put("appID", "HQ202106151645581404721836284489730");
        getTime();
        TcpCont4.put("messageID", str2);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
        TcpCont4.put("reqTime", str1);//请求时间格式
        TcpCont4.put("serviceKey", "app_login_out");//服务编码
        //2.组装报文传输
        JSONObject param4 = new JSONObject();
        param4.put("tcpCont", TcpCont4);//报文头
        param4.put("svcCont", parameters4);//报文体(加密后)
        System.out.println("<<<-APP_LOGIN_OUT发送内容ReqParam->>> " + param4);
        String agetUrl4 = HttpUtilApp.getUserCenterAddr(HttpUtilApp.APP_LOGIN_OUT, "https://10.124.131.213:8101/uac-sso-app/app_login_out");
        System.out.println(i + "请求对象：" + getHttpUti2 + ",请求url=:" + agetUrl4);
        String rs4 = HttpUtil.postJson(agetUrl4, param4.toString());
        System.out.println("===APP_LOGIN_OUT=：" + rs4);
    }

    private static void getTime() {
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    	SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
    	Date thisTime = new Date(); // new Date()为获取当前系统时间
    	str1 = df1.format(thisTime);
    	str2 = df2.format(thisTime);
    	int random = (int)(Math.random()*100000000);
    	str2 += random;
    }
}
