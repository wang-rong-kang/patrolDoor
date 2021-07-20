package com.unicom.patrolDoor.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @Author wrk
 * @Date 2021/6/9 17:18
 */
public class HttpClientUtil {
    private static final Logger logger = LogManager.getLogger(HttpClientUtil.class);

    /**
     * Get请求
     * map:调用第三方接口的参数
     * url:调用第三方接口路径
     */
    public Result transferByGet(Map map,String url) {
        Result result=new Result();
        //参数
        //Map map = new HashMap<>();
        //map.put("test", "test");

        // 构造httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //url地址
        URI uri = null;
        try {
            String jsonStr = JSON.toJSONString(map);
            uri = new URIBuilder(url).setParameter("map", jsonStr).build();
        } catch (URISyntaxException e) {
            logger.info(e.getMessage());
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");
        CloseableHttpResponse response = null;
        try {
            //执行Get请求
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                String respContent = EntityUtils.toString(httpEntity, "UTF-8");
//                System.out.println("respContent: " + respContent);
                result.setCode(ResultCode.SUCCESS);
                result.setData(respContent);
            }
        } catch (IOException e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            logger.info(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                logger.info(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Post请求
     * map:调用第三方接口的参数
     * url:调用第三方接口路径
     */
    public Result transferByPost(Map map,String url) {
        Result result=new Result();
        //参数
        //Map map = new HashMap<>();
        //map.put("test", "test");

        // 构造httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 在传送复杂嵌套对象时，一定要把对象转成json字符串，我这里实用的是alibaba.fastjson，当然你也可以使用其他的json工具
        httpPost.addHeader("Content-Type","application/json");
        httpPost.setHeader("Accept", "application/json");
        //requestEntity.setContentEncoding("UTF-8");
        String jsonStr= JSON.toJSONString(map);
        StringEntity requestEntity=new StringEntity(jsonStr,"UTF-8");
        httpPost.setEntity(requestEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                String respContent = EntityUtils.toString(httpEntity, "UTF-8");
//                System.out.println("respContent: " + respContent);
                result.setCode(ResultCode.SUCCESS);
                result.setData(respContent);
            }
        } catch (IOException e) {
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            logger.info(e.getMessage());
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
                logger.info(e.getMessage());

            }
        }
        return  result;
    }
}
