package com.yuan.utils;

import com.alibaba.fastjson.JSON;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.WebInfo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:50
 * @Description 缓存管理
 */
public class DataCacheUtil {
    //键值对集合 线程安全的Hash Table
    private final static Map<String, Entity> map = new ConcurrentHashMap<>();

    //定时器线程池，用于清除过期缓存
    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 添加缓存
     *
     * @param key  键
     * @param data 值
     */
    public static void put(String key, Object data) {
        put(key, data, 0);
    }

    /**
     * 添加缓存
     *
     * @param key    键
     * @param data   值
     * @param expire 过期时间，单位：秒， 0表示无限长
     */
    public static void put(String key, Object data, long expire) {
        //清除原键值对
        Entity entity = map.get(key);
        if (entity != null) {
            Future oldFuture = entity.getFuture();
            if (oldFuture != null) {
                oldFuture.cancel(true);
            }
        }

        //设置过期时间
        if (expire > 0) {
            Future future = executor.schedule(() -> {
                map.remove(key);
            }, expire, TimeUnit.SECONDS);
            map.put(key, new Entity(data, future));
        } else {
            //不设置过期时间
            map.put(key, new Entity(data, null));
        }
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return
     */
    public static Object get(String key) {
        Entity entity = map.get(key);
        return entity == null ? null : entity.getValue();
    }

    /**
     * 读取所有缓存
     *
     * @return
     */
    public static Collection values() {
        return map.values();
    }

    /**
     * 清除缓存
     *
     * @param key
     * @return
     */
    public static Object remove(String key) {
        //清除原缓存数据
        Entity entity = map.remove(key);
        if (entity == null) return null;
        //清除原键值对定时器
        Future future = entity.getFuture();
        if (future != null) future.cancel(true);
        return entity.getValue();
    }

    /**
     * 查询当前缓存的键值对数量
     *
     * @return
     */
    public static int size() {
        return map.size();
    }

    /**
     * 缓存实体类
     */
    private static class Entity {
        //键值对的value
        private Object value;

        //定时器Future
        private Future future;

        public Entity(Object value, Future future) {
            this.value = value;
            this.future = future;
        }

        /**
         * 获取值
         *
         * @return
         */
        public Object getValue() {
            return value;
        }

        /**
         * 获取Future对象
         *
         * @return
         */
        public Future getFuture() {
            return future;
        }
    }

    /**
     * 随机头像
     * @return
     */
    public static String getRandomAvatar() {
        WebInfo webInfo = (WebInfo) get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            String randomAvatar = webInfo.getRandomAvatar();
            List<String> randomAvatars = JSON.parseArray(randomAvatar, String.class);
            if (!CollectionUtils.isEmpty(randomAvatars)) {
               int count=randomAvatars.size();
                Random r = new Random();
                int rin = r.nextInt(count); // 生成[0,10]区间的整数
               return randomAvatars.get(rin);
            }
        }
        return null;
    }

    /**
     * 随机背景
     * @param
     * @return
     */
    public static String getRandomCover() {
        WebInfo webInfo = (WebInfo) get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            String randomCover = webInfo.getRandomCover();
            List<String> randomCovers = JSON.parseArray(randomCover, String.class);
            if (!CollectionUtils.isEmpty(randomCovers)) {

                int count=randomCovers.size();
                Random r = new Random();
                int rin = r.nextInt(count); // 生成[0,10]区间的整数
                return randomCovers.get(rin);


            }
        }
        return null;
    }
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }



}
