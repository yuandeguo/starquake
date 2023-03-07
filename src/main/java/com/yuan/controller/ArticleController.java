package com.yuan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.User;
import com.yuan.pojo.WeiYan;
import com.yuan.service.ArticleService;
import com.yuan.service.WeiYanService;
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
    public R listBossArticle(@RequestBody SearchArticleParam searchArticleParam,@RequestHeader("Authorization") String authorization) {
      IPage<ArticleVo> list= articleService.listArticle(searchArticleParam,authorization);
      return R.success(list);
    }

    /**
     *更新文章的状态
     */
    @PostMapping("/admin/updateArticleStatus")
    public R updateArticleStatus(@RequestBody ArticleUpdateStatusParams articleUpdateStatusParams) {
         return articleService.updateArticleStatus(articleUpdateStatusParams);
    }

    /**
     * 根据文章id删除文章
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteArticle")
    public R deleteArticle(@RequestParam("id") Integer id)
    {


        return articleService.deleteArticle(id);
    }

    /**
     * 更新文章时根据id查询文章
     * @param id
     * @return
     */
    @GetMapping("/admin/getArticleById")
    public R getArticleById(@RequestParam("id") Integer id) {
return articleService.getArticleById(id);


    }
    /**
     * 更新文章
     */
    @PostMapping("/admin/updateArticle")
    public R updateArticle(@Validated @RequestBody Article article, BindingResult result,@RequestHeader("Authorization") String authorization) {
        if(result.hasErrors())
            return R.fail("文章参数错误");
        article.setUpdateBy(((User)DataCacheUtil.get(authorization)).getUsername());
  return articleService.updateArticle(article);


}

    /**
     * 保存文章
     * @param article
     * @param result
     * @param authorization
     * @return
     */
    @PostMapping("/admin/saveArticle")
    public R saveArticle(@Validated @RequestBody Article article, BindingResult result,@RequestHeader("Authorization") String authorization) {
        if(result.hasErrors()) {
            return R.fail("文章参数错误");
        }

        return articleService.saveArticle(article,authorization);
    }
    /**
     * 查询文章List
     */
    @PostMapping("/listArticle")
    public R listArticle(@RequestBody SearchArticleParam searchArticleParam) {
      return  articleService.listArticleByFront(searchArticleParam);

    }

    /**
     * 获取文章信息
     * @param id
     * @return
     */
    @GetMapping("/getArticleById")
    public R getArticleByIdFront(@RequestParam("id") Integer id) {

        return articleService.getArticleByIdFront(id);
    }
}
