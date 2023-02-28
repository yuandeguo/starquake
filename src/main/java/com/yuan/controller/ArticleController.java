package com.yuan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.annotations.LoginCheck;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.service.ArticleService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:36
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 查询文章列表
     */
    @PostMapping("/admin/articleList")
    @LoginCheck(0)
    public R listBossArticle(@RequestBody SearchArticleParam searchArticleParam,@RequestHeader("Authorization") String authorization) {
        log.info("***ArticleController.listBossArticle业务结束，结果:{}",searchArticleParam );
      List<Article> list= articleService.listArticle(searchArticleParam,authorization);
return R.success(list);
    }

/**
 *
 */
@PostMapping("/admin/updateArticleStatus")
@LoginCheck(0)
public R updateArticleStatus(@RequestBody ArticleUpdateStatusParams articleUpdateStatusParams) {
    log.info("***ArticleController.listBossArticle业务结束，结果:{}",articleUpdateStatusParams );
    Article article=new Article();
    article.setId(articleUpdateStatusParams.getArticleId());
    article.setViewStatus(articleUpdateStatusParams.getViewStatus());
    article.setCommentStatus(articleUpdateStatusParams.getCommentStatus());
    article.setRecommendStatus(articleUpdateStatusParams.getRecommendStatus());
    boolean b = articleService.updateById(article);
    if(!b) {
        return R.fail("更新失败");
    }

     return R.success();
}


}
