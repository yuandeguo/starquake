package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ArticleMapper;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.User;
import com.yuan.service.ArticleService;
import com.yuan.utils.DataCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:37
 * @Description null
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Override
    public List<Article> listArticle(SearchArticleParam searchArticleParam,String  authorization) {

        QueryWrapper<Article> queryWrapper=new QueryWrapper<>();
        if (StringUtils.hasText(searchArticleParam.getSearchKey())) {
            queryWrapper.eq("article_title", searchArticleParam.getSearchKey());
        }
        if (searchArticleParam.getRecommendStatus() != null) {
            queryWrapper.eq("recommend_status", searchArticleParam.getRecommendStatus());
        }
        if (searchArticleParam.getSortId() != null) {
            queryWrapper.eq("sort_id", searchArticleParam.getSortId());
        }
        if (searchArticleParam.getLabelId() != null) {
            queryWrapper.eq("label_id", searchArticleParam.getLabelId());
        }
        if(!searchArticleParam.getIsBossSearch())//不是boss只能查看直接的文章
        {
            queryWrapper.eq("user_id", ((User)DataCacheUtil.get(authorization)).getId());
        }



        IPage<Article> page=new Page<>(searchArticleParam.getCurrent(),searchArticleParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        List<Article> records = page.getRecords();

        log.info("***ArticleServiceImpl.listArticle业务结束，结果:{}", records);


        return records;
    }
}
