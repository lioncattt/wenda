package com.cj.wendaplatform.interceptor;

import com.cj.wendaplatform.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cj
 * @date 2019/7/20
 * 权限控制拦截器
 * 拦截某些需要登录的页面 /user/question/userid (homecontroller中)
 * 拦截后把当前页面记录到next中 ，传到登录页面(存到model中)，登录时把next提交
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(hostHolder.getUser() == null) { //当前token已过期或者cookie已失效或未登录需重新登录
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()
                    +"/user/toLogin?next="+httpServletRequest.getRequestURI());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
