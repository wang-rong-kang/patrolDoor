package com.unicom.patrolDoor.utils;

import java.util.Scanner;

/**
 * @Author wrk
 * @Date 2021/5/21 10:03
 */
public class EmailUtil {
    public String emailVerification(String email, String cloudEmail) {
        String isNo = "1";
        Scanner sc = new Scanner(email);
        String mail = sc.next();
        int count = 0;
        for (int i = 0; i < mail.length(); i++) {
            char chat = mail.charAt(i);
            if (chat == '@') {
                count++;
            }
            if (count > 1) {
                isNo = "格式错误 存在多个@";
                System.exit(0);
                return isNo;
            }
        }
        //最后出现.的位置
        int pointIndex = mail.lastIndexOf(".");
        int aindex = mail.indexOf("@");

        if (pointIndex < aindex) {
            isNo = "格式错误,@后没有.";
            System.exit(0);
            return isNo;
        }

        if (aindex + 1 >= pointIndex) {
            isNo = "格式错误,@和.之前没字符";
            System.exit(0);
            return isNo;
        }
        if (aindex == 0) {
            isNo = "格式错误,@在第一位";
            System.exit(0);
            return isNo;
        }
        String format = email.substring(aindex + 1, pointIndex);
        if (format.equals("163") || format.equals("126") || format.equals("qq") || format.equals("gmail") || format.equals("sina") || format.equals("139") || format.equals(cloudEmail)) {
            isNo = "1";
        } else {
            isNo = "邮箱格式错误";
        }
        String suffix = email.substring(pointIndex + 1, email.length());
        if (suffix == null || suffix.equals("")) {
            isNo = "邮箱后缀错误";
            return isNo;
        } else {
            if (suffix.equals("com") || suffix.equals("cn")) {
                isNo = "1";
            } else {
                isNo = "邮箱后缀错误";
                return isNo;
            }
        }
        return isNo;
    }
}
