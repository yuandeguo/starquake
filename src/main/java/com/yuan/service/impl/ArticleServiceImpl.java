package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ArticleMapper;
import com.yuan.mapper.CommentMapper;
import com.yuan.myEnum.CommentTypeEnum;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.Comment;
import com.yuan.pojo.Sort;
import com.yuan.pojo.User;
import com.yuan.service.*;
import com.yuan.utils.DataCacheUtil;
import com.yuan.vo.ArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:37
 * @Description null
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private SortService sortService;
    @Resource
    private LabelService labelService;
    @Resource
    private UserService userService;
    @Resource
    private CommentService commentService;



    @Override
    public IPage<ArticleVo> listArticle(SearchArticleParam searchArticleParam, String  authorization) {

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
        IPage<ArticleVo> articleIPage=new Page<>();
        List<ArticleVo> collect=new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
             collect = records.stream().map(article -> {
                ArticleVo articleVO = buildArticleVO(article);
                return articleVO;
            }).collect(Collectors.toList());
        }
        articleIPage.setRecords(collect);
        articleIPage.setTotal(page.getTotal());
        log.info("***ArticleServiceImpl.listArticle业务结束，结果:{}", articleIPage.getRecords());

        return articleIPage;
    }
    /**
     * 封装对象
     * @param article
     * @return
     */
    private ArticleVo buildArticleVO(Article article){
        ArticleVo articleVO = new ArticleVo();
        BeanUtils.copyProperties(article, articleVO);
        if (!StringUtils.hasText(articleVO.getArticleCover())) {
           // 没有封面就随机封面，还没做好
            articleVO.setArticleCover("http://rqldcqw23.hn-bkt.clouddn.com/articleCover/Sara11677397871718631.jpg");
        }
        articleVO.setSort(sortService.getById(articleVO.getSortId()));
        articleVO.setLabel(labelService.getById(articleVO.getLabelId()));
        articleVO.setUsername(userService.getById(articleVO.getUserId()).getUsername());
        QueryWrapper<Comment> commentQueryWrapper=new QueryWrapper<>();
        commentQueryWrapper.eq("type", CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode());
        commentQueryWrapper.eq("source", articleVO.getId());
        articleVO.setCommentCount(commentService.count(commentQueryWrapper));
        return articleVO;
    }


}
