package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.utils.R;
import com.yuan.vo.ArticleVo;

import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:36
 * @Description null
 */
public interface ArticleService extends IService<Article> {
    R listArticle(SearchArticleParam searchArticleParam, String authorization);

    R listArticleByFront(SearchArticleParam searchArticleParam);



    R saveArticle(Article article, String authorization);

    R updateArticleStatus(ArticleUpdateStatusParams articleUpdateStatusParams);

    R deleteArticle(Integer id);

    R getArticleById(Integer id);

    R updateArticle(Article article,String authorization);

    R getArticleByIdFront(Integer id);
}
