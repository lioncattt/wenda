package com.cj.wendaplatform.service;

import com.cj.wendaplatform.error.BusinessException;
import com.cj.wendaplatform.model.LoginTicket;
import com.cj.wendaplatform.model.User;

import java.util.Map;

/**
 * @author cj
 * @date 2019/7/16
 */

public interface UserService {

    User selectUserById(Integer id);

    Map<String, Object> register(User user) throws BusinessException;

    int registerByUsername(String username);

    Map<String, Object> login(String name, String password) throws BusinessException;

    int registerByPhone(String phoneNumber);

    void logout(String ticket);

    User selectUserByName(String name);

    LoginTicket selectByTicket(String ticket);
}
