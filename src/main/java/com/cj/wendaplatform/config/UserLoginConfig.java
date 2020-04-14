package com.cj.wendaplatform.config;

import com.cj.wendaplatform.interceptor.LoginRequiredInterceptor;
import com.cj.wendaplatform.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author cj
 * @date 2019/7/20
 * 设置登录相关的拦截器，将其通过springIoc生成到spring容器中
 */
@Component
public class UserLoginConfig extends WebMvcConfigurerAdapter {
     @Autowired
    PassportInterceptor passportInterceptor;

     @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns(
                "/user/profile/*", "/msg/**");
        super.addInterceptors(registry);
    }
}
