package com.unicom.patrolDoor.interceptor;

import com.alibaba.fastjson.JSON;
import com.unicom.patrolDoor.dao.UserMapper;
import com.unicom.patrolDoor.entity.User;
import com.unicom.patrolDoor.utils.Result;
import com.unicom.patrolDoor.utils.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;


/**
 * @Author wrk
 * @Date 2021/5/10 9:26
 */
//拦截器  主要是判断用户登陆没登陆
public class MyInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;

    private static final Logger log = LogManager.getLogger(HandlerInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ParseException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") || requestURI.equals("/user/register") || requestURI.equals("/user/forgetPassword") || requestURI.equals("/user/out") || requestURI.equals("/verificationCode/create") || requestURI.equals("/user/upload") || requestURI.equals("/fileInfo/upload")) {
            return true;
        } else {
            //当前的token是用户的账号
            String token = ((HttpServletRequest) request).getHeader("token");
            if (StringUtils.isNotBlank(token)) {
                List<User> userList = userMapper.selectByNumber(token);
                if (userList.get(0).getStatu() == 1) {
                    if (userList.size() > 0 && userList.get(0).getOnLine() == 1) {
                        return true;
                    }
                    Result result = new Result();
                    result.setCode(ResultCode.FAIL);
                    result.setMessage("访问失败,请重新登录");
                    returnErrorResponse(response, result);
                    return false;
                } else {
                    Result result = new Result();
                    result.setCode(ResultCode.FAIL);
                    result.setMessage("访问失败,请重新登录");
                    returnErrorResponse(response, result);
                    return false;
                }
            } else {
                log.error("----------" + requestURI + "接口中----------token是空" + "----------");
                Result result = new Result();
                result.setCode(ResultCode.FAIL);
                result.setMessage("访问失败,请重新登录");
                returnErrorResponse(response, result);
                return false;
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    //将结果输出给前端
    public void returnErrorResponse(HttpServletResponse response, Result result)
            throws IOException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JSON.toJSONString(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
