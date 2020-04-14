package com.cj.wendaplatform.interceptor;

import com.cj.wendaplatform.model.HostHolder;
import com.cj.wendaplatform.model.LoginTicket;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author cj
 * @date 2019/7/20
 * 登录校验
 * 校验ticket，根据ticket查询出对应用户，存放到hostholder和model中
 * 使所有类和页面都能获取得到user
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {


    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override  //校验登录用户的cookie中的ticket,存储对应user至上下文
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies() != null) {
            for(Cookie cookie : httpServletRequest.getCookies()) {
                if(cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if(ticket != null) {
            //查询出当前用户对应token
            LoginTicket loginTicket = userService.selectByTicket(ticket);
            //若查询出当前token已过期或者cookie已失效
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) ||
                    loginTicket.getStatus() != 0) {
                return true;
            }
            User user = userService.selectUserById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }

        return true;
    }

    @Override //在所有controller渲染之前，先把user放到model中，这样每个页面都能获取user的值
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
