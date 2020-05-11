package com.cj.wendaplatform.service.impl;

import com.cj.wendaplatform.model.Like;
import com.cj.wendaplatform.service.LikeService;
import com.cj.wendaplatform.util.JedisAdapter;
import com.cj.wendaplatform.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author cj
 * @date 2019/7/29
 * 回答的点赞功能实现
 * 每个回答都对应一个like set 和 dislike set
 * set中每个kv绑定一个用户的赞或踩
 * 页面上只显示赞的次数
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override //查询回答赞的次数
    public long getLikeCount(int entityType, int entityId) {
        //查询当前回答对应的likekey
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        //查询当前回答赞的个数
        return jedisAdapter.scard(likeKey);
    }

    @Override //查询当前用户在问题详情页面的回答下like的状态(是赞过还是踩过)
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        //判断当前回答对应的like是否包含当前user的赞
        if(jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        //被当前用户踩过返回-1 没踩没赞过返回0
        return jedisAdapter.sismember(
                dislikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    @Override //对某个回答插入一个赞 一个回答对应一个key，一个key对应多个userid
    public long like(int userId, int entityType, int entityId) {
        //定义一个唯一的key
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        //插入对应kv(v为userId)
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
       //把之前踩过的kv删除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    @Override
    public long disLike(int userId, int entityType, int entityId) {
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));
        //把之前赞过的kv删除
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 批量同步点赞到数据库
     * @param likeList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int synLikeAndDisLikeToDataBase(List<Like> likeList) {

        return 0;
    }

}
