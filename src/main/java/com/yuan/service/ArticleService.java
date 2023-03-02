package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.vo.ArticleVo;

import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:36
 * @Description null
 */
public interface ArticleService extends IService<Article> {
    IPage<ArticleVo> listArticle(SearchArticleParam searchArticleParam, String authorization);

    IPage<ArticleVo> listArticleByFront(SearchArticleParam searchArticleParam);

    ArticleVo getByIdToFront(Integer id);
}
