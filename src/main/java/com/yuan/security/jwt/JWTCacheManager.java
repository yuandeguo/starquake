package com.yuan.security.jwt;

import io.lettuce.core.support.caching.RedisCache;
import lombok.Data;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/13 23:38
 * @Description null
 */
@Data
public class JWTCacheManager  extends AbstractCacheManager {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<?,?> redisTemplate;

    private String name;

    private long expire = 3600;

    @Override
    protected Cache createCache(String name) {
        return new JWTCache(redisTemplate,name, expire);
    }

    public JWTCacheManager(String name, long expire) {
        this.name = name;
        this.expire = expire;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

}
