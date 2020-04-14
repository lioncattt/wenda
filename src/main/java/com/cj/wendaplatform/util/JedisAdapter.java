package com.cj.wendaplatform.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * @author cj
 * @date 2019/7/29
 * 封装jedis中的方法
 */
@Component
public class JedisAdapter implements InitializingBean {

    private JedisPool jedisPool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
         //初始化jedis连接池
        jedisPool = new JedisPool("127.0.0.1", 6379);
    }

    //设置验证码有效时间
    public String setExKey(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public String getValueByKey(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                return jedis.get(key);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long sadd(String key, String value) { //给set添加一个kv
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) { //删除set中的一个kv
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) { //获取key的个数
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) { //判断该元素是否在set中
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     *当没有元素可以被弹出时返回一个 nil 的多批量值，并且 timeout 过期。
     * 当有元素弹出时会返回一个双元素的多批量值，
     * 其中第一个元素是弹出元素的 key，第二个元素是 value
     * @param timeout 阻塞时间 若list中无元素，则阻塞直到timeout
     * @param key
     * @return
     */
    public List<String> brpop(int timeout, String key) { //
          Jedis jedis = null;
          try {
              jedis = jedisPool.getResource();
              return jedis.brpop(timeout, key);
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              if (jedis != null) {
                  jedis.close();
              }
          }
          return null;
    }

    public long lpush(String key, String value) { //往list左边push一个元素
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    //遍历list中的元素
    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value) { //zset添加一对kv
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long zrem(String key, String value) { //zset删除一对kv
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public Transaction multi(Jedis jedis) { //开启事务
        try {
            return jedis.multi();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 执行事务中的所有命令
     * @param tx
     * @param jedis
     * @return 每条命令对应的返回值
     */
    public List<Object> exec(Transaction tx, Jedis jedis) { //执行事务
        try {
            return tx.exec();
        } catch (Exception e) {
            tx.discard();
            e.printStackTrace();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrange(String key, int start, int end) { //升序遍历找出zset中对应key的value存到set中
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) { //降序遍历
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) { //返回key对应的value个数
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String memeber) { //获得某一个kv对应的score
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zscore(key, memeber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


}
