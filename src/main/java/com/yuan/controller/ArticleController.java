package com.yuan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.annotations.LoginCheck;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.User;
import com.yuan.service.ArticleService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import com.yuan.vo.ArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
      List<ArticleVo> list= articleService.listArticle(searchArticleParam,authorization);
return R.success(list);
    }

/**
 *更新文章的状态
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

    /**
     * 根据文章id删除文章
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteArticle")
    public R deleteArticle(@RequestParam("id") Integer id)
    {
        boolean b = articleService.removeById(id);
        if(!b) {
            return R.fail("更新失败");
        }
        return R.success();
    }

    /**
     * 更新文章时根据id查询文章
     * @param id
     * @return
     */
    @GetMapping("/admin/getArticleById")
    public R getArticleById(@RequestParam("id") Integer id) {
        Article byId = articleService.getById(id);
        if(byId==null)
        {
            return R.fail("错误，文章不存在");
        }
        return R.success(byId);
    }
/**
 * 更新文章
 */
@PostMapping("/admin/updateArticle")
public R updateArticle(@Validated @RequestBody Article article, BindingResult result,@RequestHeader("Authorization") String authorization) {
    if(result.hasErrors())
        return R.fail("文章参数错误");
    log.info("***ArticleController.updateArticle业务结束，结果:{}", article);
    article.setUpdateBy(((User)DataCacheUtil.get(authorization)).getUsername());
    boolean b = articleService.updateById(article);
    if(!b)
    {
        return R.fail("文章更新失败");
    }
    return R.success();
}
    @PostMapping("/admin/saveArticle")
    public R saveArticle(@Validated @RequestBody Article article, BindingResult result,@RequestHeader("Authorization") String authorization) {
        if(result.hasErrors()) {
            return R.fail("文章参数错误");
        }
        if(!article.getViewStatus())
        {
            if(!StringUtils.hasText(article.getPassword()))
            {
                return R.fail("文章参数错误");
            }
        }
        article.setUserId(((User)DataCacheUtil.get(authorization)).getId());
        article.setCreateTime(LocalDateTime.now());
        boolean b = articleService.save(article);
        if(!b)
        {
            return R.fail("文章保存失败");
        }
        return R.success();
    }




}
