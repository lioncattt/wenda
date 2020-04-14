package com.cj.wendaplatform.controller;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.response.ResponseData;
import com.cj.wendaplatform.service.UserService;
import com.cj.wendaplatform.util.AliyunSendSmsEntry;
import com.cj.wendaplatform.util.JedisAdapter;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author cj
 * @date 2019/7/18
 * 用户功能 登录注册
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {


    private final UserService userService;

    private final JedisAdapter jedisAdapter;

    @Autowired
    public UserController(UserService userService, JedisAdapter jedisAdapter) {
        this.userService = userService;
        this.jedisAdapter = jedisAdapter;
    }

    /**
     * 校验当前用户名是否已存在
     * @param name
     * @return
     */
    @GetMapping(path = {"/regName/{name}"})
    @ResponseBody
    public ResponseData validateRegisterName(@PathVariable("name") String name) {
        return ResponseData.create("result", userService.registerByUsername(name));
    }

    /**
     * 判断当前手机号是否已被注册
     * @param phoneNumber
     * @return
     */
    @GetMapping(path = {"/regPhone/{phoneNumber}"})
    @ResponseBody
    public ResponseData validatePhone(@PathVariable("phoneNumber") String phoneNumber) {
        return ResponseData.create("phoneMsg", userService.registerByPhone(phoneNumber));
    }

    /**
     * 调用阿里云短信通道给对应手机发送验证码
     * @return
     */
    @PostMapping(path = {"/reg/{phoneNumber}"})
    @ResponseBody
    public ResponseData getSmsCaptcha(@PathVariable("phoneNumber") String phoneNumber) throws BusinessException {
        //生成验证码
        String captcha = AliyunSendSmsEntry.generateCaptcha();
        //调用阿里云发送验证码
        String msg = AliyunSendSmsEntry.sendSms(phoneNumber, captcha);
        //优化: 存到redis中10分钟后过期(将验证码存到session中 供注册时校验)
        //session.setAttribute("captcha", captcha);
        jedisAdapter.setExKey(RedisKeyUtil.getRegCaptcha(phoneNumber), 60, captcha);
        return ResponseData.create("msg", msg);
    }

    /**
     * 注册用户
     * @param user
     * @param response
     * @param next 记录未登录/注册前浏览的页面的url(非必须)
     * @param captcha 验证码
     * @return
     * @throws BusinessException
     */
    @PostMapping("/reg")
    @ResponseBody
    public ResponseData register(User user,  HttpServletResponse response,
                           @RequestParam(value = "captcha") String captcha,
                           @RequestParam(value = "phone") String phone,
                           @RequestParam(value = "next", required = false) String next) throws BusinessException {
        //判断验证码是否正确
        if(StringUtils.isBlank(captcha)) { //判空
            throw new BusinessException(EmBusinessError.USER_CAPTCHA_NOT_EXIST);
        }
        /*if(!captcha.equals(session.getAttribute("captcha"))) {
            throw new BusinessException(EmBusinessError.USER_CAPTCHA_ERROR);
        } else {
            session.setAttribute("captcha", null);//验证码正确则清空session
        }*/
        if (captcha.equals(jedisAdapter.getValueByKey(RedisKeyUtil.getRegCaptcha(phone)))) {
            jedisAdapter.delKey(RedisKeyUtil.getRegCaptcha(phone));
        } else { //验证码错误或已过期
            throw new BusinessException(EmBusinessError.USER_CAPTCHA_ERROR);
        }

        Map<String, Object> map = userService.register(user);

        //判断是否注册成功
        if(map.containsKey("ticket")) {
            //注册成功有ticket
            Cookie cookie = new Cookie("ticket",
                    map.get("ticket").toString());

            //将cookie路径设置为所有web请求路径都能读取该cookie，
            //默认只有当前/reg路径下的请求才可读取
            cookie.setPath("/");
            response.addCookie(cookie);

            if(StringUtils.isNotBlank(next)) {
                //返回对应map信息

                return ResponseData.create("successMsg", 0); //返回未登录前页面
            }

            return ResponseData.create("successMsg", 1);//跳转至首页
        }

        //没有生成ticket注册失败，跳转回注册页面
        return ResponseData.create("successMsg", 2);
    }

    /**未登录用户访问需登录页面时拦截器拦截该请求并执行toLoginPage()方法
     * 拦截器先获取之前的url，把next添加到当前请求后面/tologin?next=```
     * 该方法再将next存到model，让登录和注册页面获取，
     * @param model
     * @param next
     * @return
     */
    @GetMapping(path = {"/toLogin"})
    public String toLoginPage(Model model, @RequestParam(value = "next",
            required = false) String next) {
        if(StringUtils.isNotBlank(next)) {
            model.addAttribute("next", next);
        }
        return "login";
    }

    @GetMapping(path = {"/toRegister"})
    public String toRegisterPage(Model model, @RequestParam(value = "next",
            required = false) String next) {
        if(StringUtils.isNotBlank(next)) {
            model.addAttribute("next", next);
        }
        return "register";
    }



    @PostMapping(path = {"/login"})
    @ResponseBody
    public ResponseData login(String name, String password,
                              @RequestParam(value = "next", required = false) String next,
                              @RequestParam(value="rememberMe", defaultValue = "false") boolean rememberMe,
                              HttpServletResponse response) throws BusinessException {
       Map<String, Object> map = userService.login(name, password);
       if(map.containsKey("ticket")) {
           //把标识用户身份的token(ticket)存到cookie中，使浏览器每次请求都带上ticket
           Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
           cookie.setPath("/");

           if(rememberMe) { //记住我
               cookie.setMaxAge(3600 * 24 * 5);//cookie有效期为5天
           }
           response.addCookie(cookie);

           if(StringUtils.isNotBlank(next)) {
               //0跳转至登录前页面
               return ResponseData.create("successMsg", 0);
           }
           return ResponseData.create("successMsg", 1);//1至首页
       }
        //2没有生成对应的ticket，userServiceImpl中抛出异常，不会执行该return登录失败
        return ResponseData.create("successMsg", 2);
    }

    /**
     * 登出 更新token中的status的状态为cookie已过期
     * @param ticket
     * @return
     */
    @GetMapping(path = {"/logout"})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }


}
