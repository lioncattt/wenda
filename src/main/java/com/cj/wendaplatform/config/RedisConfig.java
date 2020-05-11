package com.cj.wendaplatform.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @date 2020/4/12
 */

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 申明缓存管理器，会创建一个切面（aspect）并触发Spring缓存注解的切点（pointcut）
     * 根据类或者方法所使用的注解以及缓存的状态，这个切面会从缓存中获取数据，
     * 将数据添加到缓存之中或者从缓存中移除某个值
     */



    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory factory) {
        // 创建一个模板类
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        // 设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化器
        //使用Jackson 2，将对象序列化为JSON
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //json转对象类，不设置默认的会将json转成hashmap
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;

    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        // 默认超时时间 单位秒
        redisCacheManager.setDefaultExpiration(60);
        // 缓存超时时间Map, key为缓存对象键名 value为超时时间，单位为秒
        Map<String, Long> expiresMap = new HashMap<>();
        // 缓存用户信息
        expiresMap.put("user", 1800L);
        // 缓存token信息
        expiresMap.put("token", 600L);
        redisCacheManager.setExpires(expiresMap);
        return redisCacheManager;
    }

    /**
     * 由于RedisTemplate并没提供scan方法，需自己设置
     * @param redisTemplate
     * @param match
     * @param count
     * @return
     */
    public static Cursor<String> scan(RedisTemplate redisTemplate, String match, int count) {
        ScanOptions options = ScanOptions.scanOptions()
                .count(count)
                .match(match).build();
        RedisSerializer<String> redisSerializer = redisTemplate.getKeySerializer();
        Cursor<String> cursor = (Cursor)redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                        redisSerializer::deserialize)
        );
        return cursor;
    }


}
