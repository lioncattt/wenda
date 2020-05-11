package com.cj.wendaplatform.task;

import com.cj.wendaplatform.config.RedisConfig;
import com.cj.wendaplatform.model.Like;
import com.cj.wendaplatform.service.LikeService;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author cj
 * @date 2020/4/15
 * 定时任务：将Redis数据同步到MySQL
 */

@Component
public class SynRedis2MySQL {

    @Autowired
    private LikeService likeService;

    @Autowired
    private RedisTemplate redisTemplate;

    // scan每次查询keys的个数
    private static final int COUNT = 1000;


    /**
     * 同步点赞点踩数据到数据库
     */
    @Scheduled(fixedDelay = 1000)
    @Async("taskExecutor")
    public void synLikesToMySQL() {
        try {
            System.out.println("开始同步点赞数据到数据库");
            long start = System.currentTimeMillis();
            String likeKeyPrefix = RedisKeyUtil.getLikeAndDisLikeKeyPrefix();
            // 使用scan取代keys* 防止数据量过大造成阻塞
            Cursor<String> keyCursor = RedisConfig.scan(redisTemplate, likeKeyPrefix, COUNT);
            SetOperations setOperations = redisTemplate.opsForSet();
            List<String> likeKeys = new ArrayList<>();
            // 将查询到的键值对封装到list中
            List<Like> likeList = new ArrayList<>();
            // 遍历获取所有likeKeys
            while (keyCursor.hasNext()) {
                likeKeys.add(keyCursor.next());
            }
            // 使用完游标必须立即关闭后再执行其他操作，否则会
            keyCursor.close();

            // 根据获取的keys查询对应的set
            if (likeKeys != null && !likeKeys.isEmpty()) {
                for (String key : likeKeys) {
                    Cursor valueCursor = setOperations.scan(key, ScanOptions.NONE);
                    StringBuilder likeValues = new StringBuilder();

                    // 遍历出所有的值
                    while (valueCursor.hasNext()) {
                        Integer likeValue =  (Integer) valueCursor.next();
                        likeValues.append(likeValue+",");
                    }
                    valueCursor.close();
                    likeValues.deleteCharAt(likeValues.length() - 1);
                    // 解析key value
                    Like like = setLikeByRedisLike(key, likeValues.toString());
                    likeList.add(like);
                }
            }
            // 同步数据到数据库
            likeService.synLikeAndDisLikeToDataBase(likeList);
            long end = System.currentTimeMillis();
            System.out.println("完成点赞数据同步到数据库，耗时：" + (end - start) + "毫秒");

        } catch (IOException e) {
            System.out.println("IO异常");
            e.printStackTrace();
        }

    }

    /**
     * 解析对应like和dislike键值对
     * @param key
     * @param value
     * @return
     */
    private Like setLikeByRedisLike(String key, String value) {
        Like like = new Like();
        String prefix = RedisKeyUtil.getBizLike();
        String[] arr = key.split(":");
        String[] arr2 = value.split(",");
        // 如果为点赞
        if (prefix.equals(key)) {
            like.setEntityType(Integer.parseInt(arr[1]));
            like.setEntityId(Integer.parseInt(arr[2]));
            like.setState(1);
            like.setUserId(value);
            like.setCreatedDate(new Date());
            like.setLikeCount(arr2.length);
        } else {
            // 若为点踩
            like.setEntityType(Integer.parseInt(arr[1]));
            like.setEntityId(Integer.parseInt(arr[2]));
            like.setState(0);
            like.setUserId(value);
            like.setCreatedDate(new Date());
            like.setLikeCount(arr2.length);
        }
        return like;
    }


}
