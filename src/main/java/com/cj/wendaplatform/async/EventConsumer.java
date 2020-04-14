package com.cj.wendaplatform.async;

import com.alibaba.fastjson.JSON;
import com.cj.wendaplatform.util.JedisAdapter;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @date 2019/8/1
 * 事件消费者类 用到了多种设计模式
 * 1. 取出队列中的eventModel
 * 2. 构建eventType和eventHandler的一对多的映射关系(难点)
 *    2.1. 先通过applicationContext找到EventHandler的所有实现类
 *    2.2. 遍历所有实现类EventHandler，查找每个实现类中的对应的EventType
 *    2.3 如果当前Map<EventType, List<EventHandler>> config没有EventType,则添加对应kv
 *    否则将当前EventHandler添加到对应EventType的value中
 * 提示： 通过实现ApplicatonContextAware接口可以
 * 通过appicatonContext对象动态加载/获取
 * spring容器中已经实例化的类
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    //一个eventType可能对应多个eventhandler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    //获取spring容器中的bean
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;


    @Override //初始化当前类的属性后会调用该方法
    public void afterPropertiesSet() throws Exception {
        //获取实现eventHandler接口的所有实现类 该map的key为beanNames
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        //遍历map找到当前eventType对应的eventHandler
        if(beans!=null) {
            //通过beans.entrySet()可以遍历map中的key和value，entry表示一对kv
            for(Map.Entry<String, EventHandler> entry
            : beans.entrySet()) {
                //通过entry.getValue()获取当前eventHandler实现类
                // 再获取对应的eventTypes
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for(EventType type : eventTypes) {
                    //若当前map中没有该事件类型则创建
                   if (!config.containsKey(type)) {
                       config.put(type, new ArrayList<EventHandler>());
                   }

                    // 当前EventHandler到该事件类型对应的list中
                   config.get(type).add(entry.getValue());
                }
            }
        }

        //创建子线程，实现异步监听队列，将事件从队列中取出处理
        //找到eventmodel中的eventType根据事件类型找到对应的handler处理
      new Thread(new Runnable() {
          @Override
          public void run() {
              while (true) {
                  String key = RedisKeyUtil.getEventQueueKey();
                  //若队列无元素则一直阻塞直到有event添加进来
                  //返回的第一个值是value对应的key，第二个才是value(eventModel)
                  List<String> events = jedisAdapter.brpop(0, key);
                  for(String event : events) {
                      if (event.equals(key)) {
                          continue; //过滤events中的key
                      }
                      //将存在redis中的EventModel json类型字符串转化成对应的对象
                      EventModel eventModel = JSON.parseObject(event, EventModel.class);
                      if (!config.containsKey(eventModel.getType())) {
                          System.out.println("不能识别的事件");
                          continue;
                      }

                      //根据当前eventModel的eventType找出在map中对应的处理器handler
                      for (EventHandler handler : config.get(eventModel.getType())) {
                          handler.doHandle(eventModel);
                      }
                  }
              }
          }
      }).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
          this.applicationContext = applicationContext;
    }
}
