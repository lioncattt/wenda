package com.cj.wendaplatform.async;

import com.alibaba.fastjson.JSONObject;
import com.cj.wendaplatform.util.JedisAdapter;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cj
 * @date 2019/8/1
 * 事件的生产者类: 将当前接收的event放入队列中
 */
@Service
public class
EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 把事件模型转成json类型字符串存到redis中的双向队列list中
     * @param eventModel 事件模型
     * @return true:表示入队成功
     */
    public boolean fireEvent(EventModel eventModel) {
        try {
            String jsonEventModel = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, jsonEventModel);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
