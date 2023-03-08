package com.yuan.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/8 10:10
 * @Description null
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    @Resource
    private JedisPool jedisPool;

    /**
     * 用户点赞，点赞数加一
     *
     * @param articleId
     * @return
     */
    @Override
    public boolean articleLikeUp(Integer articleId) {
        return articleDataUp("like",articleId);
    }

    /**
     * 文章热度统计
     *
     * @param articleId
     * @return
     */
    @Override
    public boolean articleHeatUp(Integer articleId) {
        return articleDataUp("heat",articleId);
    }

    /**
     * 获取缓存中热度和点赞数量
     *
     * @param articleId
     * @return
     */
    @Override
    public   Map<String, String> getArticleLikeAndHeat(Integer articleId) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "article::" + articleId.toString();
            Map<String, String> map = jedis.hgetAll(key);
            return map;
        }
        finally{
            if(null != jedis)
                jedis.close(); // 释放资源还给连接池
        }

    }

    /**
     * @param key     存储的键
     * @param o       存储的java对象
     * @param expire  设置过期时间,单位：秒，小于0时为永不过期
     */
    public  void set(String key, Object o, long expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ObjectMapper mapper = new ObjectMapper();
            //p是指定要转换的对象
            String obj = mapper.writeValueAsString(o);
            SetParams params = new SetParams();
            //单位秒
            params.ex(expire);
            jedis.set(key,obj,params);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if(null != jedis) jedis.close();//新版本的close方法，如果是从JedisPool中取出的，则会放回到连接池中，并不会销毁。
        }
    }


    public  <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String data = jedis.get(key);
            if(!StringUtils.hasText(data)){
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();

            T t = (T)  mapper.readValue(data,clazz);

            return t;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if(null!=jedis) jedis.close();//新版本的close方法，如果是从JedisPool中取出的，则会放回到连接池中，并不会销毁。
        }
    }

    @Override
    public boolean remove(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        jedis.del(key);
        }  finally {
            if(null!=jedis) jedis.close();//新版本的close方法，如果是从JedisPool中取出的，则会放回到连接池中，并不会销毁。
        }
        return true;
    }


    public boolean articleDataUp(String fields,Integer articleId)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "article::" + articleId.toString();
            if (!jedis.hexists(key, fields))
                jedis.hset(key, fields, "1");//新建一个hash数据，并只向value中添加一条【属性和属性值】
            else {
                List<String> field = jedis.hmget(key, fields);
                jedis.hset(key, fields, String.valueOf((Integer.parseInt(field.get(0)) + 1)));
            }
        }
        finally{
            if(null != jedis)
                jedis.close(); // 释放资源还给连接池
        }
        return true;
    }

}
