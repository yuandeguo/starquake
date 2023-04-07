package com.yuan.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.myEnum.TokenBucketLimiterConst;
import com.yuan.params.ArticleLikeAndViewCurrentParam;
import com.yuan.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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

            String key = "article::" + articleId.toString();
            BoundHashOperations boundHashOps = stringRedisTemplate.boundHashOps(key);
            Map<String, String> map = boundHashOps.entries();
            return map;
    }

    /**
     * @param key     存储的键
     * @param o       存储的java对象
     * @param expire  设置过期时间,单位：秒，小于0时为永不过期
     */
    public  void set(String key, Object o, long expire){

        ValueOperations valueOperations= stringRedisTemplate.opsForValue();
        ObjectMapper mapper = new ObjectMapper();
            //p是指定要转换的对象
        try {
            String json=mapper.writeValueAsString(o);
            valueOperations.set(key,json,expire, TimeUnit.SECONDS);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


    public  <T> T get(String key, Class<T> clazz) {


            String data = stringRedisTemplate.opsForValue().get(key);
            if(!StringUtils.hasText(data)){
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();

        T t = null;
        try {
            t = (T)  mapper.readValue(data,clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return t;

    }

    @Override
    public String get(String key) {
        String data = stringRedisTemplate.opsForValue().get(key);
        return data;
    }

    @Override
    public boolean remove(String key) {
    return Boolean.TRUE.equals(stringRedisTemplate.delete(key));

    }

    @Override
    public     List<ArticleLikeAndViewCurrentParam>  getAllArticleLikeAndHeat() {

            Set<String> keys=stringRedisTemplate.keys("article::**");

            List<ArticleLikeAndViewCurrentParam> list=new ArrayList<>();
            for (String Aid:keys)
            {
                String substring = Aid.substring(9);

                BoundHashOperations boundHashOps= stringRedisTemplate.boundHashOps(Aid);
                Map map = boundHashOps.entries();
                ArticleLikeAndViewCurrentParam param=new ArticleLikeAndViewCurrentParam(Integer.parseInt(substring),0,0);
                if(map!=null&&!map.isEmpty()) {
                    Integer num = 0;
                    String like =null;
                    Object like1 = map.get("like");
                    if(like1!=null)
                        like=like1.toString();
                    if (StringUtils.hasText(like)) {
                        num = Integer.parseInt(like);
                        param.setLike(num);
                    }
                    num=0;
                    String heat = null;

                    Object heat1 = map.get("heat");
                    if(heat1!=null)
                        heat=heat1.toString();


                    if (StringUtils.hasText(heat)) {
                        num = Integer.parseInt(heat);
                        param.setView(num);
                    }
                }
                list.add(param);
            }


        log.info("***RedisServiceImpl.getAllArticleLikeAndHeat业务结束，结果:{}", list);
            return list;



    }

    @Override
    public boolean deleteArticleLikeAndViewCurrentParam(List<ArticleLikeAndViewCurrentParam> list) {

            for (ArticleLikeAndViewCurrentParam item:list)
            {
                stringRedisTemplate.delete("article::"+item.getArticleId());

                stringRedisTemplate.delete(CommonConst.ARTICLE_CACHE+"ById:"+item.getArticleId());
            }
        Set<String> keys = stringRedisTemplate.keys("listArticleByFront" + '*');
        stringRedisTemplate.delete(keys);


        return true;




    }

    @Override
    public void setVisitIp(String ip) {

            stringRedisTemplate.opsForSet().add("oneDayVisitIp",ip);


    }

    @Override
    public Integer getVisitIp() {

            Set<String> oneDayVisitIp = stringRedisTemplate.opsForSet().members("oneDayVisitIp");
            return  oneDayVisitIp.size();

    }

    @Override
    public void setVisitUrl() {
        stringRedisTemplate.opsForValue().increment("oneDayVisitUrl",1);
    }
    @Override
    public Integer getVisitUrl() {


            String oneDayVisitUrl = stringRedisTemplate.opsForValue().get("oneDayVisitUrl");

        stringRedisTemplate.opsForValue().set("oneDayVisitUrl", "1");
            if(StringUtils.hasText(oneDayVisitUrl))
            return Integer.parseInt(oneDayVisitUrl);
            else return 0;

    }

    @Override
    public void removeList(String s) {
        Set<String> keys = stringRedisTemplate.keys(s);
        stringRedisTemplate.delete(keys);
    }

    @Override
    public boolean getLock(String lockKey) {
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", 1, TimeUnit.SECONDS);
        System.out.println(aBoolean);
        return BooleanUtil.isTrue(aBoolean);
    }

    @Override
    public void unlock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }

    @Override
    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    @Override
    public String limit_listPush(String limitListKey) {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(String.class);
        redisScript.setScriptText(TokenBucketLimiterConst.redLua);


        List<String> keyList = new ArrayList<>();
        keyList.add(limitListKey);

        String execute =stringRedisTemplate.execute(redisScript, keyList,String.valueOf(TokenBucketLimiterConst.intervalPerPermit)
                ,String.valueOf(System.currentTimeMillis())
        ,String.valueOf(TokenBucketLimiterConst.initTokens)
        ,String.valueOf(TokenBucketLimiterConst.bucketMaxTokens)
        ,String.valueOf(TokenBucketLimiterConst.resetBucketInterval));
return  execute;

    }


    public boolean articleDataUp(String fields,Integer articleId)
    {


        String key = "article::" + articleId.toString();
        stringRedisTemplate.opsForHash().increment(key,fields,1);
        return true;
    }

}
