package com.yuan.service;

import com.yuan.params.ArticleLikeAndViewCurrentParam;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    /**
     * 用户点赞，点赞数加一
     * @param articleId
     * @return
     */
    boolean articleLikeUp(Integer articleId);

    /**
     * 文章热度统计
     * @param articleId
     * @return
     */
    boolean articleHeatUp(Integer articleId);

    /**
     * 获取缓存中热度和点赞数量
     * @param articleId
     * @return
     */
    Map<String, String> getArticleLikeAndHeat(Integer articleId);

      void set(String key, Object o, long expire);
    <T> T get(String key, Class<T> clazz);
    String get(String key);
    boolean remove(String key);

    List<ArticleLikeAndViewCurrentParam>  getAllArticleLikeAndHeat();

    boolean deleteArticleLikeAndViewCurrentParam(List<ArticleLikeAndViewCurrentParam> list);

    void setVisitIp(String ip);
    Integer getVisitIp();
    void setVisitUrl();
    Integer getVisitUrl();


    void removeList(String s);

    boolean getLock(String lockKey);

    void unlock(String lockKey);

    StringRedisTemplate getStringRedisTemplate();

    String limit_listPush(String limitListKey);

    Long setEmptyList(String key, Object value );
   Long leftPush(String key, Object value, Long expire);
   List<String> getListRange(String key, long start, long end);







    void putHash(String hashName, String key, Object value);

    Object getHashValueByKey(String hashName, String key);

    void removeHashKey(String hashName, String key);

    void removeHash(String hashName);

    int sizeHash(String hashName);

    Set<Object> keysHash(String hashName);

    Collection<Object> valuesHash(String hashName);

}
