package com.unicom.patrolDoor.utils;

/**
 * @Author wrk
 * @Date 2021/5/13 15:23
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 开启 mvc支持，设置 static 目录为类路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value(value = "${file.send}")
    private String send;

    @Value(value = "${file.file}")
    private String fileRoot;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("file:"+ Const.Report_Path);
        registry
                .addResourceHandler("/file/**")
//                .addResourceLocations("file:"+ "/home/aiuap_jc/forum/file" + "/");
                .addResourceLocations("file:"+ send+fileRoot+ "/");
    }
}
