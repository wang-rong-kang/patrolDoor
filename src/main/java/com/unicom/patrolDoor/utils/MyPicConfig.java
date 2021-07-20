package com.unicom.patrolDoor.utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author wrk
 * @Date 2021/4/8 13:39
 */
@Configuration
public class MyPicConfig implements WebMvcConfigurer {

    @Value(value = "${file.send}")
    private String send;

    @Value(value = "${file.file}")
    private String fileRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/"+fileRoot+"**").addResourceLocations("file:"+send+fileRoot);
    }
}