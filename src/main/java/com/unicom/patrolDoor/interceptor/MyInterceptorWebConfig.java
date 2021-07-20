package com.unicom.patrolDoor.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/**
 * @Author wrk
 * @Date 2021/6/18 17:37
 */

@Configuration
public class MyInterceptorWebConfig extends WebMvcConfigurerAdapter {
    //这里必须创建自定义拦截器的对象  不创建对象  在MyInterceptor类中注入mapper失败
    @Bean
    public MyInterceptor myFilter() {
        return new MyInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns = {//拦截所有请求
                "/**"
        };
        /**
         *   /user/login:登陆接口
         *   /user/register：注册接口
         *   /user/forgetPassword
         *   /file/** 静态资源
         */
        String[] excludePatterns = {//去除哪些拦截请求
                "/user/login", "/user/register","/user/forgetPassword","/user/out","/verificationCode/create","/error","/fileInfo/upload","/flush/flush","/file/**"
        };

        //如果有多个拦截器，可以继续添加, 但是多个拦截器的对象都得创建
        registry.addInterceptor(myFilter()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePatterns);

    }

}

