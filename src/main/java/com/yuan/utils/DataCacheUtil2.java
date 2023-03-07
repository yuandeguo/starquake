package com.yuan.utils;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 15:41
 * @Description null
 */
@Service
public class DataCacheUtil2 {
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 保存问题和回答，实现连续对话
     * @param key
     * @param value
     */
    public void SetString(String key,String value)
    {
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set(key, value, Duration.ofMinutes(10));
    }

    /**
     * 获取上次的问题和答案，实现连续对话
     * @param key
     * @return
     */
    public String getString(String key)
    {
        ValueOperations ops = redisTemplate.opsForValue();
        String res = (String) ops.get(key);
        return res;

    }




}
