package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.service.FollowService;
import com.cj.wendaplatform.util.JedisAdapter;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author cj
 * @date 2019/8/2
 * A关注B
 * B粉丝列表：key：关注的实体类型 关注的实体id value：A的id
 * A关注的实体列表: key: A的id  关注的实体类型
 * value: 实体id
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户关注某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return 返回事务是否执行成功
     */
    public boolean follow(int userId, int entityType, int entityId) {
        //粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        //实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //粉丝列表
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        //关注列表
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        //每条命令对应的返回值
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
    }


    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unfollow(int userId, int entityType, int entityId) {
        //粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        //实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //粉丝列表
        tx.zrem(followerKey, String.valueOf(userId));
        //关注列表
        tx.zrem(followeeKey, String.valueOf(entityId));
        //每条命令对应的返回值
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
    }

    /**
     * 获取粉丝列表
     * @param entityType 实体类型
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Integer> getFollowers(int entityType, int entityId,
                                      int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset + limit));
    }

    /**
     * 获取关注列表
     * @param userId 当前用户id
     * @param entityType 关注实体类型
     * @param offset
     * @param count
     * @return
     */
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset + count));
    }

    /**
     * 获取粉丝的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }


    /**
     * 获取关注的数量
     * @param userId 当前用户id
     * @param entityType 关注的对象类型
     * @return
     */
    public long getFolloweeCount(int userId, int entityType) {
         String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    /**
     * 将查询出的zset中key对应的value转化为Integer类型
     * @param idset
     * @return
     */
    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str: idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


    /**
     * 判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }


}
