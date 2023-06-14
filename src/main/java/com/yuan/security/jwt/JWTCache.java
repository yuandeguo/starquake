package com.yuan.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.service.RedisService;
import lombok.Data;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/13 23:41
 * @Description null
 */
@Data
public class JWTCache<K, V> implements Cache<K, V>{

    private String name;

    private long expire;


    private String KEY_PREFFIX = "shiro_takeout_";;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    public JWTCache(RedisTemplate redisTemplate,String name,Long expire) {
        this.name = name;
        this.expire = expire;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K key) throws CacheException {
        String sKey = KEY_PREFFIX + serializerKey(key);
        return (V) redisTemplate.opsForValue().get(sKey);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (key == null) {
            return value;
        }
        try {
            String redisCacheKey = KEY_PREFFIX + serializerKey(key);
            redisTemplate.opsForValue().set(redisCacheKey,value, expire, TimeUnit.SECONDS);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = KEY_PREFFIX+serializerKey(key);
            Object previous = redisTemplate.opsForValue().get(redisCacheKey);
            redisTemplate.delete(redisCacheKey);
            return (V) previous;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void clear() throws CacheException {

        Set<String> keys = null;
        try {
            keys = redisTemplate.keys(this.KEY_PREFFIX + "*");
        } catch (Exception e) {

        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (String key: keys) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public int size() {
        Long longSize = 0L;
        Set<String> keys = null;
        try {
            keys = redisTemplate.keys(this.KEY_PREFFIX + "*");
            longSize = new Long(keys.size());
        } catch (Exception e) {

        }
        return longSize.intValue();
    }

    public Set<K> keys() {
        Set<String> keys = null;
        try {
            return redisTemplate.keys(this.KEY_PREFFIX + "*");
        } catch (Exception e) {

            return Collections.emptySet();
        }
    }

    @Override
    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisTemplate.keys(this.KEY_PREFFIX + "*");
        } catch (Exception e) {

            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (String key : keys) {
            V value = null;
            try {
                value =  (V) redisTemplate.opsForValue().get(key);
            } catch (Exception e) {

            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    private String serializerKey(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        String className = obj.getClass().getName();
        return className + Objects.hashCode(obj);
    }

    public Boolean lock(String key,long timeout) {
        String lockKey = "LOCK_" + key;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, 1);
        if (flag) {
            redisTemplate.expire(lockKey, timeout, TimeUnit.SECONDS);
        }
        return flag;
    }

    public void unLock(String key) {
        String lockKey = "LOCK_" + key;
        redisTemplate.delete(lockKey);
    }

    public boolean isLock(String key) {
        String lockKey = "LOCK_" + key;
        Object object = redisTemplate.opsForValue().get(lockKey);
        if (object != null) {
            return true;
        }
        return false;
    }

}
