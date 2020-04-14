package com.cj.wendaplatform.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.cj.wendaplatform.dao.LoginTicketMapper;
import com.cj.wendaplatform.dao.UserMapper;
import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.error.EmBusinessError;
import com.cj.wendaplatform.model.LoginTicket;
import com.cj.wendaplatform.model.User;
import com.cj.wendaplatform.service.UserService;
import com.cj.wendaplatform.validator.ValidationResult;
import com.cj.wendaplatform.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author cj
 * @date 2019/7/16
 *
 *
 */
@Service
public class UserServiceImpl implements UserService {


    private final  UserMapper userMapper;

    private final LoginTicketMapper loginTicketMapper;

    private final ValidatorImpl validator;


    @Autowired
    public UserServiceImpl(UserMapper userMapper, LoginTicketMapper loginTicketMapper, ValidatorImpl validator) {
        this.userMapper = userMapper;
        this.loginTicketMapper = loginTicketMapper;
        this.validator = validator;
    }


    @Override  //根据id查询用户
    @Cacheable(value = "user", key = "'user:id:'+ #id.toString()")
    public User selectUserById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "token", key = "'token:' + #ticket")
    public LoginTicket selectByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    //校验当前用户名是否已经被注册 1为已被注册，0为未被注册
    @Override
    public int registerByUsername(String username) {
        return userMapper.selectByName(username) != null ? 1 : 0;
    }

    //校验当前手机号是否已被注册 1为已被注册，0为未被注册
    @Override
    public int registerByPhone(String phoneNumber) {
        return userMapper.selectByPhone(phoneNumber) != null ? 1 : 0;
    }

    @Override //登录校验
    public Map<String, Object> login(String name, String password) throws BusinessException {
        Map<String, Object> map = new HashMap<>();//存放ticket
        if(StringUtils.isBlank(name) || StringUtils.isBlank(password)) {//校验用户名和密码
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //校验数据库是否有该用户
        User validateUser = userMapper.selectByName(name);

        if(validateUser == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        if(!SecureUtil.md5(password+validateUser.getSalt()).equals(validateUser.getPassword())) {
           throw new BusinessException(EmBusinessError.USER_PASSWORD_ERROR);
        }
        //通过校验，发放ticket
        String ticket = addLoginTicket(validateUser.getId());
        if(StringUtils.isBlank(ticket)) {
            throw new BusinessException(EmBusinessError.USER_INSERT_TICKET_ERROR);
        }
        map.put("ticket", ticket);
        return map;
    }

    //登出功能
    @Override
    @CachePut(value = "token",  key = "'token:' + #ticket")
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    @Override
    public User selectUserByName(String name) {
        return userMapper.selectByName(name);
    }


    /**
     * 注册用户
     * @param user
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional
    public Map<String, Object> register(User user) throws BusinessException {
      if(user == null) { //传入用户参数为空
          throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
      }
        //校验user中字段是否合法
        ValidationResult result = validator.validate(user);

        if(result.isHasErrors()) { //校验结果为有错误
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        //=====================================================添加用户

        Map<String, Object> map = new HashMap<String, Object>();
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        //设置默认头像
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        //加盐
        user.setPassword(SecureUtil.md5(user.getPassword()+user.getSalt()));
        userMapper.addUser(user);//插入成功后返回主键到参数user中
        System.out.println("userId" + user.getId());

        if( user.getId()> 0) {
            //插入成功则登录,发放ticket, 一段uuid
            String ticket = addLoginTicket(user.getId());
            if(StringUtils.isBlank(ticket)) {
                throw new BusinessException(EmBusinessError.USER_INSERT_TICKET_ERROR);
            }
            map.put("ticket", ticket);
            //登录
            return map;
        }

        //插入失败，抛出自定义异常
        throw new BusinessException(EmBusinessError.DATABASE_ERROR);
    }

    //注册或登录成功后生成对应user的ticket

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        //设置过期时间为1day
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        ticket.setExpired(date);
        ticket.setStatus(0); //表示cookie有效
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketMapper.addTicket(ticket);//插入ticket
        return ticket.getTicket();
    }
}
