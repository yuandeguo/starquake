package com.yuan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuan.params.ArticleLikeAndViewCurrentParam;

import java.util.List;
import java.util.Map;

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
}
