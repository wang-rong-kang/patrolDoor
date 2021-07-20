package com.unicom.patrolDoor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @Author wrk
 * @Date 2021/4/21 9:48
 */

public class SensitivewordFilter {

    public static void main(String[] args) throws Exception {
        SensitivewordFilter sensitivewordFilterTest=new SensitivewordFilter();
        String file ="D://sensitive.txt";
        System.out.println(sensitivewordFilterTest.replaceSensitiveWord("腐败", 1,"*",file));
    }

    //读取敏感词文件  获取到敏感词文件的内容
    public List<String> readSensitiveWordByFile(String filePath) throws Exception{

        List<String> list = null;

        //File file = new File("msc/SensitiveWord.txt"); //读取文件
        File file = new File(filePath);  //读取文件
        InputStreamReader read = new InputStreamReader(new FileInputStream(file),"UTF-8");
        try {
            if(file.isFile() && file.exists()){ //文件流是否存在
                list = new ArrayList<String>();
                BufferedReader bufferedReader = new BufferedReader(read);
                String txt = null;
                while((txt = bufferedReader.readLine()) != null){ //读取文件，将文件内容放入到set中
                    String[] strSplit=txt.trim().split(",");
                    for(String str:strSplit){
                        list.add(str);
                    }
                }
            }else{ //不存在抛出异常信息
                throw new Exception("词库文件不存在");
            }
        } catch (Exception e) {
            throw e;
        }finally{
            read.close(); //关闭文件流
        }
        return list;
    }

//将敏感词内容添加到map中
    private Map addSensitiveWord(List<String> datas) {
        Map sensitiveWordMap = new HashMap(datas.size());
        Iterator<String> iterator = datas.iterator();
        Map<String, Object> now = null;
        Map now2 = null;
        while (iterator.hasNext()) {
            now2 = sensitiveWordMap;
            String word = iterator.next().trim(); //敏感词
            for (int i = 0; i < word.length(); i++) {
                char key_word = word.charAt(i);
                Object obj = now2.get(key_word);
                if (obj != null) { //存在
                    now2 = (Map) obj;
                } else { //不存在
                    now = new HashMap<String, Object>();
                    now.put("isEnd","0");
                    now2.put(key_word, now);
                    now2 = now;
                }
                if (i == word.length() - 1) {
                    now2.put("isEnd","1");
                }
            }
        }
        return  sensitiveWordMap;
    }

    //根据输入的内容  判断是否包含敏感词
    public List<String> getSensitiveWord(String text, int matchType,Map sensitiveWordMap) {
        List<String> words = new ArrayList<String>();
        Map now = sensitiveWordMap;
        int count = 0; //初始化敏感词长度
        int start = 0; //标志敏感词开始的下标
        for (int i = 0; i < text.length(); i++) {
            char key = text.charAt(i);
            now = (Map) now.get(key);
            if (now != null) { //存在
                count++;
                if (count ==1) {
                    start = i;
                }
                if ("1".equals(now.get("isEnd"))) { //敏感词结束
                    now = sensitiveWordMap; //重新获取敏感词库
                    words.add(text.substring(start, start + count)); //取出敏感词，添加到集合
                    count = 0; //初始化敏感词长度
                }
            } else { //不存在
                now = sensitiveWordMap;//重新获取敏感词库
                if (count == 1 && matchType == 1) { //不最佳匹配
                    count = 0;
                } else if (count == 1 && matchType == 2) { //最佳匹配
                    words.add(text.substring(start, start + count));
                    count = 0;
                }
            }
        }
        return words;
    }


    //提取敏感词内容
    public String replaceSensitiveWord(String txt, int matchType, String replaceChar,String filePath) throws Exception {
        SensitivewordFilter test=new SensitivewordFilter();
        String resultTxt = txt;
        List<String> set = test.getSensitiveWord(txt, matchType, test.returnMap(filePath)); //获取所有的敏感词
/**
 *    以下是替换成*的逻辑
 */
//        System.err.println(set);
//        Iterator<String> iterator = set.iterator();
//        String word = null;
//        String replaceString = null;
//        while (iterator.hasNext()) {
//            word = iterator.next();
//            replaceString = getReplaceChars(replaceChar, word.length());
//            resultTxt = resultTxt.replaceAll(word, replaceString);
//        }
        /**
         * 以下是提取的逻辑
         */
        if (set.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < set.size(); i++) {
                buffer.append(set.get(i)+",");
            }
            resultTxt = buffer.toString().substring(0,buffer.toString().length()-1);
        } else {
            resultTxt = "false";
        }
        return resultTxt;
    }

    public  Map returnMap(String filePath) throws Exception {
        SensitivewordFilter test=new SensitivewordFilter();
        Map map=test.addSensitiveWord(test.readSensitiveWordByFile(filePath));
        return  map;
    }

}

