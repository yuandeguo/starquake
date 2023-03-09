package com.yuan.schedu;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuan.mapper.VisitNumMapper;
import com.yuan.params.ArticleLikeAndViewCurrentParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.VisitNum;
import com.yuan.service.ArticleService;
import com.yuan.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/8 16:39
 * @Description null
 */
@Component
@Slf4j
public class UpdateArticleOnTime {

        @Autowired
    RedisService redisService;
        @Resource
    ArticleService articleService;
@Resource
    VisitNumMapper visitNumMapper;
        //每小时执行一次
        @Scheduled(cron = "0 30 * * * ? ")
        public void scheduled() {
            List<ArticleLikeAndViewCurrentParam> allArticleLikeAndHeat = redisService.getAllArticleLikeAndHeat();


            for (ArticleLikeAndViewCurrentParam item: allArticleLikeAndHeat)
            {
                UpdateWrapper<Article> updateWrapper=new UpdateWrapper<>();
                updateWrapper.eq("id",item.getArticleId());
                updateWrapper.setSql("view_count = view_count + "+item.getView());
                updateWrapper.setSql("like_count = like_count + "+item.getLike());

                boolean update = articleService.update(updateWrapper);
                log.info("***UpdateArticleOnTime.scheduled业务结束inFor，结果:{}",update );
            }
            boolean b = redisService.deleteArticleLikeAndViewCurrentParam(allArticleLikeAndHeat);
            log.info("***UpdateArticleOnTime.scheduled业务结束inFor，结果:{}",b );

        }

    @Scheduled(cron = "0 0 1 * * ? ")
    public void scheduled2() {
        Integer numIp=redisService.getVisitIp();
        VisitNum visitNum=new VisitNum();
        visitNum.setNum(numIp);
        visitNum.setWriteTime(LocalDateTime.now());
        visitNum.setType(1);
        visitNumMapper.insert(visitNum);
        Integer numUrl=redisService.getVisitUrl();
        visitNum.setNum(numUrl);
        visitNum.setType(2);
        visitNumMapper.insert(visitNum);
        redisService.remove("oneDayVisitUrl");
        redisService.remove("oneDayVisitIp");




    }
}
