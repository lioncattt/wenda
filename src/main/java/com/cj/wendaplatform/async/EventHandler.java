package com.cj.wendaplatform.async;

import java.util.List;

/**
 * @author cj
 * @date 2019/8/1
 * 事件处理器抽象接口:
 * 1. 一个事件应对应一个具体的handler来处理
 * 2. 构建handler和eventType的映射关系
 * 一个handler可能会涉及多个事件类型：比如
 * 事件为login类型，handler还需要发邮件(mail类型)，
 */
public interface EventHandler {

    //当前事件模型对应的具体处理方法
    void doHandle(EventModel model);

    //一个eventHandler对应多个eventType
    List<EventType> getSupportEventTypes();
}
