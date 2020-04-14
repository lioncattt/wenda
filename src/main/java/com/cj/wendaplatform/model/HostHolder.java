package com.cj.wendaplatform.model;

import org.springframework.stereotype.Component;

/**
 * @author cj
 * @date 2019/7/20
 * 该类作用是存储拦截器查询出来的user，供其他类使用(通过autowired)
 * 设置ThreadLocal存储拦截器通过token查询出来的user，保证线程隔离
 * 每个线程都有一个user的副本，相互独立
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users =
            new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
