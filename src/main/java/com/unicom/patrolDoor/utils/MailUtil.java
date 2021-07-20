package com.unicom.patrolDoor.utils;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @Author wrk
 * @Date 2021/5/19 16:10
 */
//@Component
//public class MailUtil {
//
//    @Scheduled(cron = "0/30 * * * * ?")//每30秒执行一次
//    public  void   bb() throws MessagingException, UnsupportedEncodingException {
//        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
//        javaMailSender.setHost("smtp.qq.com");// 链接服务器
//        javaMailSender.setUsername("957531828@qq.com");// 邮箱账号
//        javaMailSender.setPassword("xrrkiqqugufpbegh");// 授权码
//        javaMailSender.setDefaultEncoding("UTF-8");
//
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setFrom("957531828@qq.com", "通知");
//        helper.setTo(new String[]{"w_rongkang@163.com"});
//        helper.setSubject("密码更改");
//        /**
//         * 生成8位的随机密码
//         */
//        StringBuilder str = new StringBuilder();//定义变长字符串
//        Random random = new Random();
//        //随机生成数字，并添加到字符串
//        for (int i = 0; i < 8; i++) {
//            str.append(random.nextInt(10));
//        }
//        helper.setText("您的新密码是:"+str.toString(), false);
//        System.out.println(MD5Util.getMD5(str.toString()));
//        javaMailSender.send(message);
//    }
//}
